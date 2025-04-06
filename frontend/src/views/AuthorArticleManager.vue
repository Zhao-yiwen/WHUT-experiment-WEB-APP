<template>
  <div class="author-article-manager-container">
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
            default-active="4-2"
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
          <div class="author-article-manager">
            <div class="page-header">
              <el-page-header @back="goBack" :content="authorName + '的文章管理'"></el-page-header>
            </div>
            
            <!-- 作者信息卡片 -->
            <el-card class="author-info-card" shadow="hover">
              <div class="author-profile">
                <div class="author-avatar">
                  <el-avatar :size="80" :src="authorAvatar"></el-avatar>
                </div>
                <div class="author-details">
                  <h2>{{ authorName }}</h2>
                  <div class="info-grid">
                    <div class="info-item">
                      <i class="el-icon-message"></i>
                      <span>邮箱：{{ authorInfo.email || '未设置' }}</span>
                    </div>
                    <div class="info-item">
                      <i class="el-icon-wallet"></i>
                      <span>余额：{{ authorInfo.balance || 0 }} 元</span>
                    </div>
                    <div class="info-item">
                      <i class="el-icon-date"></i>
                      <span>生日：{{ authorInfo.birthday || '未设置' }}</span>
                    </div>
                    <div class="info-item">
                      <i class="el-icon-document"></i>
                      <span>文章数量：{{ authorArticles.length }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
            
            <!-- 搜索和操作按钮 -->
            <div class="table-operations">
              <div class="left-operations">
                <el-input
                  placeholder="搜索文章标题"
                  v-model="searchQuery"
                  clearable
                  @clear="handleSearchClear"
                  prefix-icon="el-icon-search"
                  style="width: 250px; margin-right: 10px">
                </el-input>
                <el-button type="primary" @click="handleSearch" size="small">搜索</el-button>
              </div>
              <div class="right-operations">
                <el-button type="success" @click="showAddDialog" size="small">
                  <i class="el-icon-plus"></i> 新增文章
                </el-button>
              </div>
            </div>
            
            <!-- 文章列表 -->
            <el-table
              :data="filteredArticles"
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
                prop="title"
                label="文章标题"
                width="180"
                align="left">
              </el-table-column>
              <el-table-column
                prop="content"
                label="文章内容"
                align="left"
                show-overflow-tooltip>
                <template slot-scope="scope">
                  <div class="content-preview">{{ scope.row.content }}</div>
                </template>
              </el-table-column>
              <el-table-column
                label="操作"
                width="150"
                align="center"
                fixed="right">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="showEditDialog(scope.row)">编辑</el-button>
                  <el-button
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页 -->
            <div class="pagination-container">
              <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pagination.currentPage"
                :page-sizes="[5, 10, 15, 20]"
                :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="filteredArticles.length">
              </el-pagination>
            </div>
          </div>
          
          <!-- 添加/编辑文章对话框 -->
          <el-dialog
            :title="dialogStatus === 'add' ? '新增文章' : '编辑文章'"
            :visible.sync="dialogVisible"
            width="50%">
            <el-form
              :model="articleForm"
              :rules="articleRules"
              ref="articleForm"
              label-width="100px">
              <el-form-item label="文章标题" prop="title">
                <el-input v-model="articleForm.title" placeholder="请输入文章标题"></el-input>
              </el-form-item>
              <el-form-item label="文章内容" prop="content">
                <el-input
                  type="textarea"
                  v-model="articleForm.content"
                  :rows="6"
                  placeholder="请输入文章内容">
                </el-input>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="dialogVisible = false">取 消</el-button>
              <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </span>
          </el-dialog>
          
          <!-- 查看文章内容对话框 -->
          <el-dialog
            title="文章内容"
            :visible.sync="contentDialogVisible"
            width="60%">
            <div v-if="selectedArticle">
              <h3>{{ selectedArticle.title }}</h3>
              <div class="article-meta">
                <span>分类：{{ selectedArticle.category }}</span>
                <span>发布日期：{{ selectedArticle.publishDate }}</span>
                <span>阅读量：{{ selectedArticle.viewCount }}</span>
              </div>
              <div class="article-content">
                {{ selectedArticle.content || '暂无内容' }}
              </div>
            </div>
            <span slot="footer" class="dialog-footer">
              <el-button @click="contentDialogVisible = false">关闭</el-button>
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
  name: 'AuthorArticleManager',
  data() {
    return {
      authorId: null,
      authorName: '',
      authorInfo: {
        email: 'author@example.com',
        balance: 1000,
        birthday: '1990-01-01'
      },
      authorAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      authorArticles: [],
      searchQuery: '',
      tableLoading: false,
      pagination: {
        currentPage: 1,
        pageSize: 10
      },
      dialogVisible: false,
      contentDialogVisible: false,
      dialogStatus: 'add', // 'add' or 'edit'
      selectedArticle: null,
      articleForm: {
        id: null,
        title: '',
        content: '',
        publishDate: new Date().toISOString().slice(0, 10),
        category: '技术',
        viewCount: 0
      },
      articleRules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      },
      // 模拟文章数据
      articlesData: [
        { id: 1, authorId: 1, title: '如何有效提高编程技能', category: '技术', publishDate: '2025-04-01', viewCount: 1200, content: '本文介绍了提高编程技能的有效方法，包括每日编码练习、参与开源项目、阅读高质量代码等多种实用技巧。通过系统性学习和持续实践，任何人都能逐步提升自己的编程能力。' },
        { id: 2, authorId: 1, title: 'JavaScript中的闭包详解', category: '技术', publishDate: '2025-04-05', viewCount: 890, content: '闭包是JavaScript中一个强大而复杂的特性，本文从基础概念开始，通过实例详细讲解了闭包的工作原理、使用场景以及常见陷阱，帮助读者全面理解这一重要概念。' },
        { id: 3, authorId: 1, title: 'Vue3新特性介绍', category: '技术', publishDate: '2025-04-10', viewCount: 1500, content: 'Vue3带来了Composition API、Teleport、Fragments等多项重要更新，本文详细介绍这些新特性的使用方法和适用场景，帮助开发者快速上手Vue3的新功能。' },
        { id: 4, authorId: 1, title: 'React vs Vue：如何选择', category: '技术', publishDate: '2025-04-15', viewCount: 2000, content: '本文客观比较了React和Vue两大前端框架的优缺点，从性能、学习曲线、社区支持、适用场景等多个维度进行分析，帮助开发者根据项目需求做出更合适的技术选择。' },
        { id: 5, authorId: 1, title: '前端工程化实践', category: '技术', publishDate: '2025-04-20', viewCount: 1700, content: '随着前端项目规模不断扩大，工程化变得日益重要。本文分享了前端工程化的最佳实践，包括模块化、组件化、构建工具、CI/CD流程等方面的实用经验。' },
        { id: 6, authorId: 1, title: 'CSS布局技巧', category: '技术', publishDate: '2025-04-25', viewCount: 980, content: '本文总结了CSS布局的各种技巧，包括Flexbox、Grid、响应式设计等，通过实际案例展示如何解决常见的布局问题，使页面设计更加灵活和精确。' },
        { id: 7, authorId: 1, title: '响应式设计原则', category: '设计', publishDate: '2025-04-28', viewCount: 1100, content: '随着移动设备的普及，响应式设计变得至关重要。本文介绍了响应式设计的核心原则和实现方法，包括流式布局、弹性图片、媒体查询等技术，帮助开发者创建在各种设备上都能良好显示的网站。' },
        { id: 8, authorId: 1, title: '移动端开发注意事项', category: '技术', publishDate: '2025-04-30', viewCount: 850, content: '移动端开发面临许多特殊挑战，本文总结了开发过程中需要注意的各种问题，包括触摸事件、性能优化、设备适配等，提供了一系列实用的解决方案和最佳实践。' },
        { id: 9, authorId: 2, title: '人工智能入门指南', category: '技术', publishDate: '2025-04-02', viewCount: 2500, content: '本文为人工智能初学者提供了全面的入门指南，介绍了AI的基本概念、主要研究方向、学习路径和实用工具，帮助读者快速进入这个充满机遇的领域。' },
        { id: 10, authorId: 2, title: '机器学习算法比较', category: '技术', publishDate: '2025-04-07', viewCount: 1800, content: '本文比较了常见的机器学习算法，包括线性回归、决策树、支持向量机、神经网络等，分析了它们的原理、优缺点和适用场景，帮助读者选择最适合自己项目的算法。' }
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
    },
    filteredArticles() {
      if (!this.searchQuery) {
        return this.authorArticles
      }
      
      const query = this.searchQuery.toLowerCase()
      return this.authorArticles.filter(article => 
        article.title.toLowerCase().includes(query)
      )
    },
    displayedArticles() {
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      return this.filteredArticles.slice(start, end)
    }
  },
  created() {
    this.initPageData()
  },
  methods: {
    ...mapActions(['logout']),
    
    // 初始化页面数据
    initPageData() {
      this.authorId = parseInt(this.$route.query.authorId)
      this.authorName = this.$route.query.authorName || '未知作者'
      
      // 加载作者文章数据
      this.tableLoading = true
      
      // 尝试从本地存储获取数据
      const savedArticles = localStorage.getItem('authorArticlesData')
      if (savedArticles) {
        try {
          this.articlesData = JSON.parse(savedArticles)
        } catch (error) {
          console.error('解析保存的文章数据时出错:', error)
        }
      }
      
      // 模拟从API获取数据
      setTimeout(() => {
        // 通过作者ID筛选文章
        this.authorArticles = this.articlesData.filter(article => article.authorId === this.authorId)
        this.tableLoading = false
      }, 500)
    },
    
    // 返回文章管理页面
    goBack() {
      this.$router.push('/articles')
    },
    
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
    
    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1
    },
    
    // 清除搜索
    handleSearchClear() {
      this.searchQuery = ''
      this.pagination.currentPage = 1
    },
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.pagination.pageSize = val
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val
    },
    
    // 显示添加对话框
    showAddDialog() {
      this.dialogStatus = 'add'
      this.articleForm = {
        id: null,
        title: '',
        content: '',
        publishDate: new Date().toISOString().slice(0, 10),
        category: '技术',
        viewCount: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.articleForm.clearValidate()
      })
    },
    
    // 显示编辑对话框
    showEditDialog(article) {
      this.dialogStatus = 'edit'
      this.articleForm = { ...article }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.articleForm.clearValidate()
      })
    },
    
    // 处理表单提交
    handleSubmit() {
      this.$refs.articleForm.validate(valid => {
        if (valid) {
          if (this.dialogStatus === 'add') {
            // 添加文章
            const newArticle = {
              ...this.articleForm,
              id: Date.now(), // 模拟生成ID
              authorId: this.authorId,
              viewCount: 0
            }
            
            // 模拟API调用
            setTimeout(() => {
              this.authorArticles.unshift(newArticle)
              
              // 更新全局文章列表
              this.articlesData.unshift(newArticle)
              
              // 使用本地存储保存当前文章列表状态
              localStorage.setItem('authorArticlesData', JSON.stringify(this.articlesData))
              // 保存作者ID和文章数量的映射
              const authorCount = this.authorArticles.length
              localStorage.setItem(`authorArticleCount-${this.authorId}`, authorCount)
              
              this.dialogVisible = false
              this.$message.success('添加文章成功')
            }, 300)
          } else {
            // 编辑文章
            const index = this.authorArticles.findIndex(item => item.id === this.articleForm.id)
            
            if (index !== -1) {
              // 模拟API调用
              setTimeout(() => {
                this.authorArticles.splice(index, 1, { ...this.articleForm })
                
                // 更新全局文章列表
                const articleIndex = this.articlesData.findIndex(item => item.id === this.articleForm.id)
                if (articleIndex !== -1) {
                  this.articlesData.splice(articleIndex, 1, { ...this.articleForm })
                  
                  // 使用本地存储保存当前文章列表状态
                  localStorage.setItem('authorArticlesData', JSON.stringify(this.articlesData))
                }
                
                this.dialogVisible = false
                this.$message.success('编辑文章成功')
              }, 300)
            }
          }
        }
      })
    },
    
    // 删除文章
    handleDelete(article) {
      this.$confirm(`确认删除文章《${article.title}》?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟删除操作
        const index = this.authorArticles.findIndex(item => item.id === article.id)
        
        if (index !== -1) {
          // 模拟API调用
          setTimeout(() => {
            this.authorArticles.splice(index, 1)
            
            // 更新全局文章列表
            const articleIndex = this.articlesData.findIndex(item => item.id === article.id)
            if (articleIndex !== -1) {
              this.articlesData.splice(articleIndex, 1)
              
              // 使用本地存储保存当前文章列表状态，以便在返回文章管理页面时能够获取更新后的数据
              localStorage.setItem('authorArticlesData', JSON.stringify(this.articlesData))
              // 保存作者ID和文章数量的映射，用于更新主页的作者文章计数
              const authorCount = this.authorArticles.length
              localStorage.setItem(`authorArticleCount-${this.authorId}`, authorCount)
            }
            
            this.$message.success('删除成功')
          }, 300)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    }
  }
}
</script>

<style scoped>
.author-article-manager-container {
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

.page-header {
  margin-bottom: 20px;
}

.author-info-card {
  margin-bottom: 20px;
}

.author-profile {
  display: flex;
  align-items: center;
}

.author-avatar {
  margin-right: 20px;
}

.author-details h2 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item i {
  margin-right: 8px;
  color: #409EFF;
}

.table-operations {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-operations, .right-operations {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.article-meta {
  color: #909399;
  margin: 10px 0 20px;
  display: flex;
  justify-content: space-between;
  max-width: 500px;
}

.article-content {
  line-height: 1.8;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
  margin-top: 15px;
  white-space: pre-line;
}

.content-preview {
  white-space: normal;
  word-break: break-all;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
</style> 