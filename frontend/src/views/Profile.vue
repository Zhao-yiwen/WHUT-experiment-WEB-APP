<template>
  <div class="profile-container">
    <el-container>
      <el-header>
        <div class="header-container">
          <div class="logo">个人管理系统</div>
          <div class="user-info">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="40" :src="userAvatar"></el-avatar>
                <span class="username">{{ username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            default-active="2"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <el-menu-item index="1" @click="$router.push('/home')">
              <i class="el-icon-menu"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-menu-item index="2">
              <i class="el-icon-user"></i>
              <span slot="title">个人资料</span>
            </el-menu-item>
            <el-menu-item index="3" @click="$router.push('/home')">
              <i class="el-icon-date"></i>
              <span slot="title">日历</span>
            </el-menu-item>
            
            <el-submenu index="4">
              <template slot="title">
                <i class="el-icon-s-grid"></i>
                <span>功能菜单</span>
              </template>
              <el-menu-item index="4-1" @click="$router.push('/contacts')">
                <i class="el-icon-user"></i>
                <span>联系人管理</span>
              </el-menu-item>
              <el-menu-item index="4-2" @click="$router.push('/articles')">
                <i class="el-icon-document"></i>
                <span>文章管理</span>
              </el-menu-item>
            </el-submenu>
            
            <el-menu-item index="5">
              <i class="el-icon-brush"></i>
              <span slot="title">主题切换</span>
            </el-menu-item>
            <el-menu-item index="6">
              <i class="el-icon-s-promotion"></i>
              <span slot="title">导航三</span>
            </el-menu-item>
            <el-menu-item index="7">
              <i class="el-icon-s-opportunity"></i>
              <span slot="title">导航四</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="个人资料" name="profile">
              <el-card class="profile-card">
                <div class="avatar-container">
                  <el-avatar :size="100" :src="previewImage || userAvatar"></el-avatar>
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :http-request="handleAvatarUpload"
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload">
                    <el-button size="small" type="primary" :loading="loading">
                      {{ loading ? '上传中' : '更换头像' }}
                    </el-button>
                  </el-upload>
                </div>
                <el-form :model="profileForm" label-width="80px" class="profile-form">
                  <el-form-item label="用户名">
                    <el-input v-model="profileForm.username" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="邮箱">
                    <el-input v-model="profileForm.email" disabled></el-input>
                  </el-form-item>
                  <el-form-item label="生日">
                    <el-date-picker
                      v-model="profileForm.birthday"
                      type="date"
                      placeholder="选择日期"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                      disabled>
                    </el-date-picker>
                  </el-form-item>
                  <el-form-item label="余额">
                    <el-input v-model="profileForm.balance" disabled>
                      <template slot="append">元</template>
                    </el-input>
                  </el-form-item>
                </el-form>
                
                <div class="dangerous-actions">
                  <el-divider content-position="left">危险操作</el-divider>
                  <el-button type="danger" @click="showDeleteConfirm">注销账户</el-button>
                </div>
              </el-card>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-card class="password-card">
                <el-form :model="passwordForm" status-icon :rules="rules" ref="passwordForm" label-width="100px">
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input type="password" v-model="passwordForm.oldPassword" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" v-model="passwordForm.newPassword" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input type="password" v-model="passwordForm.confirmPassword" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="updatePassword('passwordForm')" :loading="pwdLoading">修改密码</el-button>
                  </el-form-item>
                </el-form>
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog
      title="确认注销账户"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>注销账户将永久删除您的所有数据，此操作不可恢复！</span>
      <div class="delete-confirm-input">
        <span>请输入您的密码确认：</span>
        <el-input 
          type="password" 
          v-model="deleteConfirmPassword"
          placeholder="请输入密码"
          show-password>
        </el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteAccount" :loading="deleteLoading">确认注销</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { updatePassword } from '@/api/auth'
import { updateUserProfile, deleteUser } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'profile',
      loading: false,
      pwdLoading: false,
      imageUrl: '',
      deleteDialogVisible: false,
      deleteConfirmPassword: '',
      deleteLoading: false,
      profileForm: {
        username: '',
        email: '',
        birthday: '',
        avatar: null,
        balance: '0.00'
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['user']),
    username() {
      return this.user ? this.user.username : '用户'
    },
    userAvatar() {
      console.log('头像URL值:', this.user && this.user.avatar ? this.user.avatar : '无头像');
      console.log('完整的用户对象:', this.user);
      
      return this.user && this.user.avatar 
        ? this.user.avatar 
        : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    },
    previewImage() {
      return this.imageUrl;
    }
  },
  created() {
    this.initForm()
  },
  watch: {
    user: {
      handler(newVal) {
        if (newVal) {
          this.initForm()
        }
      },
      deep: true
    }
  },
  methods: {
    ...mapActions(['logout', 'updateProfile']),
    initForm() {
      if (this.user) {
        this.profileForm = {
          username: this.user.username,
          email: this.user.email,
          birthday: this.user.birthday,
          avatar: null,
          balance: this.user.balance ? this.user.balance : '0.00'
        }
      }
    },
    handleCommand(command) {
      if (command === 'profile') {
        // 已在个人资料页
      } else if (command === 'logout') {
        this.logout()
        this.$message.success('退出登录成功')
        this.$router.push('/login')
      }
    },
    handleAvatarUpload(options) {
      this.profileForm.avatar = options.file
      this.imageUrl = URL.createObjectURL(options.file)
      
      // 调用API更新头像
      this.loading = true
      console.log('准备上传的头像文件:', options.file);
      
      updateUserProfile({avatar: options.file})
        .then(res => {
          // 打印完整响应信息
          console.log('头像上传成功, 完整响应:', res);
          console.log('更新后的用户数据:', res.data);
          console.log('头像URL:', res.data.avatar);
          
          // 更新store中的用户信息
          this.updateProfile(res.data)
          this.$message.success('头像更新成功')
          
          // 清除本地预览，使用服务器返回的URL
          setTimeout(() => {
            this.imageUrl = '';
          }, 1000);
        })
        .catch(err => {
          console.error('头像上传错误:', err);
          if (err.response) {
            console.error('错误响应:', err.response);
            console.error('状态码:', err.response.status);
            console.error('响应数据:', err.response.data);
          }
          
          let errorMsg = '头像更新失败'
          if (err.response && err.response.data) {
            errorMsg = typeof err.response.data === 'string' 
              ? err.response.data 
              : err.response.data.message || '上传失败'
          }
          this.$message.error(errorMsg)
        })
        .finally(() => {
          this.loading = false
        })
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    updatePassword(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.pwdLoading = true
          try {
            await updatePassword(this.passwordForm.oldPassword, this.passwordForm.newPassword)
            this.$message.success('密码修改成功，请重新登录')
            this.logout()
            this.$router.push('/login')
          } catch (error) {
            let errorMsg = '密码修改失败';
            if (error.response && error.response.data && error.response.data.message) {
              errorMsg = error.response.data.message;
            }
            this.$message.error(errorMsg);
          } finally {
            this.pwdLoading = false
          }
        }
      })
    },
    showDeleteConfirm() {
      this.deleteDialogVisible = true;
      this.deleteConfirmPassword = '';
    },
    async deleteAccount() {
      if (!this.deleteConfirmPassword) {
        this.$message.error('请输入密码');
        return;
      }
      
      this.deleteLoading = true;
      try {
        await deleteUser(this.deleteConfirmPassword);
        this.$message.success('账户已注销');
        this.logout();
        this.$router.push('/login');
      } catch (error) {
        let errorMsg = '注销账户失败';
        if (error.response && error.response.data) {
          errorMsg = typeof error.response.data === 'string' 
            ? error.response.data 
            : error.response.data.message || '密码错误或服务器问题';
        }
        this.$message.error(errorMsg);
      } finally {
        this.deleteLoading = false;
        this.deleteDialogVisible = false;
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-header {
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
}

.header-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: white;
}

.username {
  margin: 0 10px;
}

.el-aside {
  background-color: #545c64;
  color: white;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}

.profile-card, .password-card {
  max-width: 600px;
  margin: 0 auto;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  margin-top: 10px;
}

.profile-form {
  margin-top: 20px;
}

.dangerous-actions {
  margin-top: 30px;
}

.delete-confirm-input {
  margin-top: 20px;
}

.el-divider {
  margin: 20px 0;
}
</style> 