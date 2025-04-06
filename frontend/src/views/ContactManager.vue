<template>
  <div class="contact-manager-container">
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
            default-active="4-1"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <el-menu-item index="1" @click="$router.push('/home')">
              <i class="el-icon-menu"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-menu-item index="2" @click="$router.push('/profile')">
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
          <div class="contact-manager">
            <h1>联系人管理</h1>
            
            <!-- 搜索区域 -->
            <div class="search-container">
              <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item>
                  <el-input v-model="searchForm.name" placeholder="姓名" clearable></el-input>
                </el-form-item>
                <el-form-item>
                  <el-select v-model="searchForm.province" placeholder="省份" clearable filterable>
                    <el-option
                      v-for="item in provinceOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="searchContacts">搜索</el-button>
                  <el-button @click="resetSearch">重置</el-button>
                  <el-button type="success" @click="showAddContactDialog">新增联系人</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <!-- 表格区域 -->
            <el-table
              :data="contacts"
              border
              style="width: 100%"
              v-loading="tableLoading">
              <el-table-column
                type="index"
                label="序号"
                width="60"
                align="center">
              </el-table-column>
              <el-table-column
                prop="date"
                label="日期"
                width="100"
                align="center">
              </el-table-column>
              <el-table-column
                prop="name"
                label="姓名"
                width="120"
                align="center">
              </el-table-column>
              <el-table-column
                prop="province"
                label="省份"
                width="120"
                align="center">
              </el-table-column>
              <el-table-column
                prop="city"
                label="市区"
                width="120"
                align="center">
              </el-table-column>
              <el-table-column
                prop="address"
                label="地址"
                align="center">
              </el-table-column>
              <el-table-column
                prop="zip"
                label="邮编"
                width="100"
                align="center">
              </el-table-column>
              <el-table-column
                label="操作"
                width="160"
                align="center">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                  <el-button
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页区域 -->
            <div class="pagination-container">
              <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pagination.currentPage"
                :page-sizes="[10, 20, 30, 50]"
                :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total">
              </el-pagination>
            </div>
          </div>
          
          <!-- 新增/编辑联系人对话框 -->
          <el-dialog :title="dialogType === 'add' ? '新增联系人' : '编辑联系人'" :visible.sync="dialogVisible" width="50%">
            <el-form :model="contactForm" :rules="rules" ref="contactForm" label-width="80px">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="contactForm.name"></el-input>
              </el-form-item>
              <el-form-item label="日期" prop="date">
                <el-date-picker
                  v-model="contactForm.date"
                  type="date"
                  placeholder="选择日期"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd">
                </el-date-picker>
              </el-form-item>
              <el-form-item label="省份" prop="province">
                <el-select v-model="contactForm.province" placeholder="请选择省份" @change="handleProvinceChange" filterable>
                  <el-option
                    v-for="item in provinceOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="市区" prop="city">
                <el-select v-model="contactForm.city" placeholder="请选择市区" filterable>
                  <el-option
                    v-for="item in cityOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="地址" prop="address">
                <el-input v-model="contactForm.address"></el-input>
              </el-form-item>
              <el-form-item label="邮编" prop="zip">
                <el-input v-model="contactForm.zip"></el-input>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitContactForm" :loading="submitLoading">确定</el-button>
            </span>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'ContactManager',
  data() {
    return {
      tableLoading: false,
      submitLoading: false,
      searchForm: {
        name: '',
        province: ''
      },
      contactForm: {
        id: null,
        date: '',
        name: '',
        province: '',
        city: '',
        address: '',
        zip: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        date: [{ required: true, message: '请选择日期', trigger: 'change' }],
        province: [{ required: true, message: '请选择省份', trigger: 'change' }],
        city: [{ required: true, message: '请选择市区', trigger: 'change' }],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
        zip: [
          { required: true, message: '请输入邮编', trigger: 'blur' },
          { pattern: /^[0-9]{6}$/, message: '邮编必须为6位数字', trigger: 'blur' }
        ]
      },
      contacts: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      currentIndex: -1,
      provinceOptions: [
        { value: '北京', label: '北京' },
        { value: '上海', label: '上海' },
        { value: '广东', label: '广东' },
        { value: '江苏', label: '江苏' },
        { value: '浙江', label: '浙江' },
        { value: '四川', label: '四川' }
      ],
      cityOptions: [],
      cityMap: {
        '北京': [
          { value: '东城区', label: '东城区' },
          { value: '西城区', label: '西城区' },
          { value: '朝阳区', label: '朝阳区' },
          { value: '海淀区', label: '海淀区' },
          { value: '丰台区', label: '丰台区' },
          { value: '石景山区', label: '石景山区' },
          { value: '门头沟区', label: '门头沟区' },
          { value: '房山区', label: '房山区' },
          { value: '通州区', label: '通州区' },
          { value: '顺义区', label: '顺义区' },
          { value: '昌平区', label: '昌平区' },
          { value: '大兴区', label: '大兴区' },
          { value: '怀柔区', label: '怀柔区' },
          { value: '平谷区', label: '平谷区' },
          { value: '密云区', label: '密云区' },
          { value: '延庆区', label: '延庆区' }
        ],
        '上海': [
          { value: '黄浦区', label: '黄浦区' },
          { value: '徐汇区', label: '徐汇区' },
          { value: '长宁区', label: '长宁区' },
          { value: '静安区', label: '静安区' },
          { value: '普陀区', label: '普陀区' },
          { value: '虹口区', label: '虹口区' },
          { value: '杨浦区', label: '杨浦区' },
          { value: '浦东新区', label: '浦东新区' },
          { value: '闵行区', label: '闵行区' },
          { value: '宝山区', label: '宝山区' },
          { value: '嘉定区', label: '嘉定区' },
          { value: '金山区', label: '金山区' },
          { value: '松江区', label: '松江区' },
          { value: '青浦区', label: '青浦区' },
          { value: '奉贤区', label: '奉贤区' },
          { value: '崇明区', label: '崇明区' }
        ],
        '广东': [
          { value: '广州', label: '广州' },
          { value: '深圳', label: '深圳' },
          { value: '珠海', label: '珠海' },
          { value: '汕头', label: '汕头' },
          { value: '佛山', label: '佛山' },
          { value: '韶关', label: '韶关' },
          { value: '湛江', label: '湛江' },
          { value: '肇庆', label: '肇庆' },
          { value: '江门', label: '江门' },
          { value: '茂名', label: '茂名' },
          { value: '惠州', label: '惠州' },
          { value: '梅州', label: '梅州' },
          { value: '汕尾', label: '汕尾' },
          { value: '河源', label: '河源' },
          { value: '阳江', label: '阳江' },
          { value: '清远', label: '清远' },
          { value: '东莞', label: '东莞' },
          { value: '中山', label: '中山' },
          { value: '潮州', label: '潮州' },
          { value: '揭阳', label: '揭阳' },
          { value: '云浮', label: '云浮' }
        ],
        '江苏': [
          { value: '南京', label: '南京' },
          { value: '无锡', label: '无锡' },
          { value: '徐州', label: '徐州' },
          { value: '常州', label: '常州' },
          { value: '苏州', label: '苏州' },
          { value: '南通', label: '南通' },
          { value: '连云港', label: '连云港' },
          { value: '淮安', label: '淮安' },
          { value: '盐城', label: '盐城' },
          { value: '扬州', label: '扬州' },
          { value: '镇江', label: '镇江' },
          { value: '泰州', label: '泰州' },
          { value: '宿迁', label: '宿迁' }
        ],
        '浙江': [
          { value: '杭州', label: '杭州' },
          { value: '宁波', label: '宁波' },
          { value: '温州', label: '温州' },
          { value: '嘉兴', label: '嘉兴' },
          { value: '湖州', label: '湖州' },
          { value: '绍兴', label: '绍兴' },
          { value: '金华', label: '金华' },
          { value: '衢州', label: '衢州' },
          { value: '舟山', label: '舟山' },
          { value: '台州', label: '台州' },
          { value: '丽水', label: '丽水' }
        ],
        '四川': [
          { value: '成都', label: '成都' },
          { value: '自贡', label: '自贡' },
          { value: '攀枝花', label: '攀枝花' },
          { value: '泸州', label: '泸州' },
          { value: '德阳', label: '德阳' },
          { value: '绵阳', label: '绵阳' },
          { value: '广元', label: '广元' },
          { value: '遂宁', label: '遂宁' },
          { value: '内江', label: '内江' },
          { value: '乐山', label: '乐山' },
          { value: '南充', label: '南充' },
          { value: '眉山', label: '眉山' },
          { value: '宜宾', label: '宜宾' },
          { value: '广安', label: '广安' },
          { value: '达州', label: '达州' },
          { value: '雅安', label: '雅安' },
          { value: '巴中', label: '巴中' },
          { value: '资阳', label: '资阳' },
          { value: '阿坝', label: '阿坝' },
          { value: '甘孜', label: '甘孜' },
          { value: '凉山', label: '凉山' }
        ]
      },
      // 模拟数据，实际项目中应该从后端API获取
      mockContacts: [
        { id: 1, date: '2025-04-01', name: '张三', province: '北京', city: '朝阳区', address: '朝阳路123号', zip: '100020' },
        { id: 2, date: '2025-04-02', name: '李四', province: '上海', city: '静安区', address: '南京西路456号', zip: '200040' },
        { id: 3, date: '2025-04-03', name: '王五', province: '广东', city: '深圳', address: '深南大道789号', zip: '518000' },
        { id: 4, date: '2025-04-04', name: '赵六', province: '江苏', city: '苏州', address: '金鸡湖大道101号', zip: '215000' },
        { id: 5, date: '2025-04-05', name: '钱七', province: '浙江', city: '杭州', address: '西湖区文三路202号', zip: '310000' },
        { id: 6, date: '2025-04-06', name: '孙八', province: '四川', city: '成都', address: '锦江区红星路303号', zip: '610000' },
        { id: 7, date: '2025-04-07', name: '周九', province: '北京', city: '海淀区', address: '中关村大街404号', zip: '100080' },
        { id: 8, date: '2025-04-08', name: '吴十', province: '上海', city: '徐汇区', address: '漕河泾开发区505号', zip: '200233' },
        { id: 9, date: '2025-04-09', name: '郑十一', province: '广东', city: '广州', address: '天河区天河路606号', zip: '510000' },
        { id: 10, date: '2025-04-10', name: '王十二', province: '江苏', city: '南京', address: '鼓楼区中山路707号', zip: '210000' },
        { id: 11, date: '2025-04-11', name: '李十三', province: '浙江', city: '宁波', address: '海曙区中山东路808号', zip: '315000' },
        { id: 12, date: '2025-04-12', name: '张十四', province: '四川', city: '绵阳', address: '涪城区临园路909号', zip: '621000' }
      ]
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
  created() {
    this.loadContacts()
  },
  methods: {
    ...mapActions(['logout']),
    // 处理用户下拉菜单命令
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.logout()
        this.$message.success('退出登录成功')
        this.$router.push('/login')
      }
    },
    
    // 加载联系人数据
    loadContacts() {
      this.tableLoading = true
      
      // 这里模拟从后端API获取数据的过程
      setTimeout(() => {
        // 过滤和分页
        let filteredData = [...this.mockContacts]
        
        // 应用搜索过滤
        if (this.searchForm.name) {
          filteredData = filteredData.filter(item => 
            item.name.includes(this.searchForm.name)
          )
        }
        
        if (this.searchForm.province) {
          filteredData = filteredData.filter(item => 
            item.province === this.searchForm.province
          )
        }
        
        // 更新总数
        this.pagination.total = filteredData.length
        
        // 分页处理
        const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
        const end = start + this.pagination.pageSize
        
        this.contacts = filteredData.slice(start, end)
        this.tableLoading = false
      }, 500)
    },
    
    // 搜索联系人
    searchContacts() {
      this.pagination.currentPage = 1
      this.loadContacts()
    },
    
    // 重置搜索条件
    resetSearch() {
      this.searchForm.name = ''
      this.searchForm.province = ''
      this.pagination.currentPage = 1
      this.loadContacts()
    },
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadContacts()
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadContacts()
    },
    
    // 显示新增联系人对话框
    showAddContactDialog() {
      this.dialogType = 'add'
      this.contactForm = {
        id: null,
        date: new Date().toISOString().slice(0, 10),
        name: '',
        province: '',
        city: '',
        address: '',
        zip: ''
      }
      this.cityOptions = []
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.contactForm && this.$refs.contactForm.clearValidate()
      })
    },
    
    // 处理编辑联系人
    handleEdit(index, row) {
      this.dialogType = 'edit'
      this.currentIndex = index
      this.contactForm = JSON.parse(JSON.stringify(row))
      
      // 设置对应城市选项
      this.handleProvinceChange(row.province)
      
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.contactForm && this.$refs.contactForm.clearValidate()
      })
    },
    
    // 处理删除联系人
    handleDelete(index, row) {
      this.$confirm('确认删除该联系人?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟删除操作
        this.mockContacts = this.mockContacts.filter(item => item.id !== row.id)
        this.loadContacts()
        this.$message({
          type: 'success',
          message: '删除成功!'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    
    // 处理省份变化，更新城市选项
    handleProvinceChange(province) {
      this.contactForm.city = ''
      this.cityOptions = this.cityMap[province] || []
    },
    
    // 提交联系人表单
    submitContactForm() {
      this.$refs.contactForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true
          
          setTimeout(() => {
            if (this.dialogType === 'add') {
              // 模拟新增操作
              const newContact = {
                ...this.contactForm,
                id: this.mockContacts.length > 0 ? Math.max(...this.mockContacts.map(c => c.id)) + 1 : 1
              }
              this.mockContacts.push(newContact)
              this.$message.success('添加联系人成功')
            } else {
              // 模拟更新操作
              const index = this.mockContacts.findIndex(item => item.id === this.contactForm.id)
              if (index !== -1) {
                this.mockContacts[index] = { ...this.contactForm }
                this.$message.success('更新联系人成功')
              }
            }
            
            this.dialogVisible = false
            this.submitLoading = false
            this.loadContacts()
          }, 500)
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.contact-manager-container {
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
  overflow: auto;
}

.contact-manager {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.contact-manager h1 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.search-container {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 