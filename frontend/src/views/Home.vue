<template>
  <div class="home-container">
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
            default-active="1"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <el-menu-item index="1">
              <i class="el-icon-menu"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-menu-item index="2" @click="$router.push('/profile')">
              <i class="el-icon-user"></i>
              <span slot="title">个人资料</span>
            </el-menu-item>
            <el-menu-item index="3">
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
          <el-calendar v-model="currentDate">
            <!-- 自定义内容 -->
            <template slot="dateCell" slot-scope="{date, data}">
              <div>
                <div class="calendar-date">{{ data.day.split('-').slice(1).join('-') }}</div>
                <div class="calendar-content">
                  <!-- 这里可以显示当日事件等内容 -->
                </div>
              </div>
            </template>
          </el-calendar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Home',
  data() {
    return {
      currentDate: new Date()
    }
  },
  computed: {
    ...mapGetters(['user']),
    username() {
      return this.user ? this.user.username : '用户'
    },
    userAvatar() {
      return this.user && this.user.avatar 
        ? this.user.avatar 
        : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  },
  methods: {
    ...mapActions(['logout']),
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.logout()
        this.$message.success('退出登录成功')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.home-container {
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

.calendar-date {
  font-size: 14px;
  text-align: center;
}

.calendar-content {
  min-height: 40px;
  padding-top: 5px;
}
</style> 