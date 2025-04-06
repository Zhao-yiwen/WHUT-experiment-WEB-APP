<template>
  <div class="article-manager-container">
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
          <div class="article-manager">
            <h1>文章管理</h1>
            
            <el-row :gutter="20">
              <!-- 左侧作者列表 -->
              <el-col :span="10">
                <el-card shadow="hover">
                  <div slot="header" class="clearfix">
                    <span>作者列表</span>
                  </div>
                  
                  <!-- 作者列表表格 -->
                  <el-table
                    :data="currentPageAuthors"
                    border
                    style="width: 100%"
                    v-loading="tableLoading"
                    @row-click="handleRowClick"
                    :row-class-name="getRowClassName">
                    <el-table-column
                      type="index"
                      label="序号"
                      width="60"
                      align="center">
                    </el-table-column>
                    <el-table-column
                      prop="name"
                      label="作者姓名"
                      align="center">
                    </el-table-column>
                    <el-table-column
                      prop="articleCount"
                      label="文章数量"
                      width="100"
                      align="center">
                    </el-table-column>
                    <el-table-column
                      label="操作"
                      width="150"
                      align="center">
                      <template slot-scope="scope">
                        <el-button
                          size="mini"
                          type="primary"
                          @click.stop="enterArticleManagement(scope.row)">进入文章管理</el-button>
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
                      :total="authorList.length">
                    </el-pagination>
                  </div>
                </el-card>
              </el-col>
              
              <!-- 右侧文章统计图 -->
              <el-col :span="14">
                <el-card shadow="hover">
                  <div slot="header" class="clearfix">
                    <span>文章发表统计</span>
                    <el-button
                      style="float: right; padding: 3px 0"
                      type="text"
                      @click="refreshChart">刷新</el-button>
                  </div>
                  <div id="articleChart" style="width: 100%; height: 400px;"></div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          
          <!-- 文章详情对话框 -->
          <el-dialog
            :title="selectedAuthor ? selectedAuthor.name + '的文章列表' : '文章列表'"
            :visible.sync="dialogVisible"
            width="70%">
            <el-table
              :data="authorArticles"
              border
              style="width: 100%"
              v-loading="articleLoading">
              <el-table-column
                type="index"
                label="序号"
                width="60"
                align="center">
              </el-table-column>
              <el-table-column
                prop="title"
                label="文章标题"
                align="left">
              </el-table-column>
              <el-table-column
                prop="category"
                label="分类"
                width="120"
                align="center">
              </el-table-column>
              <el-table-column
                prop="publishDate"
                label="发布日期"
                width="120"
                align="center">
              </el-table-column>
              <el-table-column
                prop="viewCount"
                label="阅读量"
                width="100"
                align="center"
                sortable>
              </el-table-column>
              <el-table-column
                label="操作"
                width="200"
                align="center">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="viewArticle(scope.row)">查看</el-button>
                  <el-button
                    size="mini"
                    type="danger"
                    @click="deleteArticle(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer">
              <el-button @click="dialogVisible = false">关闭</el-button>
            </span>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import * as echarts from 'echarts'

export default {
  name: 'ArticleManager',
  data() {
    return {
      tableLoading: false,
      articleLoading: false,
      dialogVisible: false,
      chartInstance: null,
      authorList: [
        { id: 1, name: '张三', articleCount: 8 },
        { id: 2, name: '李四', articleCount: 15 },
        { id: 3, name: '王五', articleCount: 6 },
        { id: 4, name: '赵六', articleCount: 12 },
        { id: 5, name: '钱七', articleCount: 9 },
        { id: 6, name: '孙八', articleCount: 7 },
        { id: 7, name: '周九', articleCount: 14 },
        { id: 8, name: '吴十', articleCount: 5 },
        { id: 9, name: '郑十一', articleCount: 11 },
        { id: 10, name: '王十二', articleCount: 10 },
        { id: 11, name: '赵十三', articleCount: 3 },
        { id: 12, name: '钱十四', articleCount: 18 },
        { id: 13, name: '孙十五', articleCount: 13 },
        { id: 14, name: '李十六', articleCount: 2 },
        { id: 15, name: '周十七', articleCount: 7 },
        { id: 16, name: '吴十八', articleCount: 16 },
        { id: 17, name: '郑十九', articleCount: 9 },
        { id: 18, name: '冯二十', articleCount: 14 }
      ],
      pagination: {
        currentPage: 1,
        pageSize: 5,
        total: 0
      },
      selectedAuthor: null,
      authorArticles: [],
      // 模拟文章数据
      articles: [
        { id: 1, authorId: 1, title: '如何有效提高编程技能', category: '技术', publishDate: '2025-04-01', viewCount: 1200 },
        { id: 2, authorId: 1, title: 'JavaScript中的闭包详解', category: '技术', publishDate: '2025-04-05', viewCount: 890 },
        { id: 3, authorId: 1, title: 'Vue3新特性介绍', category: '技术', publishDate: '2025-04-10', viewCount: 1500 },
        { id: 4, authorId: 1, title: 'React vs Vue：如何选择', category: '技术', publishDate: '2025-04-15', viewCount: 2000 },
        { id: 5, authorId: 1, title: '前端工程化实践', category: '技术', publishDate: '2025-04-20', viewCount: 1700 },
        { id: 6, authorId: 1, title: 'CSS布局技巧', category: '技术', publishDate: '2025-04-25', viewCount: 980 },
        { id: 7, authorId: 1, title: '响应式设计原则', category: '设计', publishDate: '2025-04-28', viewCount: 1100 },
        { id: 8, authorId: 1, title: '移动端开发注意事项', category: '技术', publishDate: '2025-04-30', viewCount: 850 },
        
        { id: 9, authorId: 2, title: '人工智能入门指南', category: '技术', publishDate: '2025-04-02', viewCount: 2500 },
        { id: 10, authorId: 2, title: '机器学习算法比较', category: '技术', publishDate: '2025-04-07', viewCount: 1800 },
        { id: 11, authorId: 2, title: '深度学习框架选择', category: '技术', publishDate: '2025-04-12', viewCount: 2100 },
        { id: 12, authorId: 2, title: '神经网络结构设计', category: '技术', publishDate: '2025-04-17', viewCount: 1950 },
        { id: 13, authorId: 2, title: '数据处理技巧', category: '技术', publishDate: '2025-04-22', viewCount: 1650 },
        { id: 14, authorId: 2, title: '自然语言处理入门', category: '技术', publishDate: '2025-04-26', viewCount: 2300 },
        { id: 15, authorId: 2, title: '图像识别最佳实践', category: '技术', publishDate: '2025-04-29', viewCount: 1850 },
        { id: 16, authorId: 2, title: '强化学习案例分析', category: '技术', publishDate: '2025-05-01', viewCount: 1550 },
        { id: 17, authorId: 2, title: 'AI伦理问题探讨', category: '观点', publishDate: '2025-05-03', viewCount: 2750 },
        { id: 18, authorId: 2, title: '机器人技术未来展望', category: '观点', publishDate: '2025-05-05', viewCount: 3100 },
        { id: 19, authorId: 2, title: '算法优化策略', category: '技术', publishDate: '2025-05-07', viewCount: 1450 },
        { id: 20, authorId: 2, title: '计算机视觉应用', category: '技术', publishDate: '2025-05-09', viewCount: 1780 },
        { id: 21, authorId: 2, title: 'ChatGPT使用技巧', category: '教程', publishDate: '2025-05-11', viewCount: 4500 },
        { id: 22, authorId: 2, title: '推荐系统设计', category: '技术', publishDate: '2025-05-13', viewCount: 1620 },
        { id: 23, authorId: 2, title: 'AI在医疗领域的应用', category: '行业', publishDate: '2025-05-15', viewCount: 2950 },
        
        { id: 24, authorId: 3, title: '摄影基础入门', category: '艺术', publishDate: '2025-04-03', viewCount: 1300 },
        { id: 25, authorId: 3, title: '风景摄影技巧', category: '艺术', publishDate: '2025-04-08', viewCount: 1450 },
        { id: 26, authorId: 3, title: '人像摄影光线控制', category: '艺术', publishDate: '2025-04-13', viewCount: 1750 },
        { id: 27, authorId: 3, title: '后期处理软件比较', category: '技术', publishDate: '2025-04-18', viewCount: 980 },
        { id: 28, authorId: 3, title: '摄影构图技巧', category: '艺术', publishDate: '2025-04-23', viewCount: 1650 },
        { id: 29, authorId: 3, title: '相机参数详解', category: '教程', publishDate: '2025-04-27', viewCount: 1100 }
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
    currentPageAuthors() {
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      return this.authorList.slice(start, end)
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.loadArticlesData()
      this.initChart()
      window.addEventListener('resize', this.resizeChart)
    })
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart)
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    ...mapActions(['logout']),
    // 加载文章数据（从本地存储或使用默认数据）
    loadArticlesData() {
      // 尝试从本地存储获取数据
      const savedArticles = localStorage.getItem('authorArticlesData')
      if (savedArticles) {
        try {
          const articlesData = JSON.parse(savedArticles)
          // 更新作者文章计数
          if (articlesData && articlesData.length > 0) {
            const authorCounts = {}
            
            // 计算每个作者的文章数量
            articlesData.forEach(article => {
              if (!authorCounts[article.authorId]) {
                authorCounts[article.authorId] = 0
              }
              authorCounts[article.authorId]++
            })
            
            // 更新作者列表中的文章计数
            this.authorList.forEach(author => {
              // 优先使用本地存储的计数，如果没有则检查本地计算的计数
              const storedCount = localStorage.getItem(`authorArticleCount-${author.id}`)
              if (storedCount !== null) {
                author.articleCount = parseInt(storedCount)
              } else if (authorCounts[author.id] !== undefined) {
                author.articleCount = authorCounts[author.id]
              }
            })
            
            // 保存更新后的文章数据
            this.articles = articlesData
          }
        } catch (error) {
          console.error('解析保存的文章数据时出错:', error)
        }
      }
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
    
    // 初始化图表
    initChart() {
      if (this.chartInstance) {
        this.chartInstance.dispose()
      }
      
      this.chartInstance = echarts.init(document.getElementById('articleChart'))
      this.updateChart()
    },
    
    // 更新图表数据
    updateChart() {
      if (!this.chartInstance) return
      
      const option = {
        title: {
          text: '当前页作者文章统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c} 篇文章'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.currentPageAuthors.map(author => author.name),
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            rotate: 30,
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          name: '文章数量',
          minInterval: 1
        },
        series: [
          {
            name: '文章数量',
            type: 'bar',
            barWidth: '60%',
            data: this.currentPageAuthors.map(author => {
              return {
                value: author.articleCount,
                itemStyle: {
                  color: this.selectedAuthor && this.selectedAuthor.id === author.id ? '#409EFF' : '#67C23A'
                }
              }
            }),
            emphasis: {
              itemStyle: {
                color: '#F56C6C'
              }
            }
          }
        ]
      }
      
      this.chartInstance.setOption(option)
    },
    
    // 处理窗口大小变化
    resizeChart() {
      if (this.chartInstance) {
        this.chartInstance.resize()
      }
    },
    
    // 刷新图表
    refreshChart() {
      this.updateChart()
    },
    
    // 处理表格行点击
    handleRowClick(row) {
      this.selectedAuthor = row
      this.updateChart()
    },
    
    // 获取行的类名
    getRowClassName({ row }) {
      return this.selectedAuthor && this.selectedAuthor.id === row.id ? 'selected-row' : ''
    },
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.selectedAuthor = null
      this.updateChart()
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.selectedAuthor = null
      this.updateChart()
    },
    
    // 进入文章管理
    enterArticleManagement(author) {
      this.selectedAuthor = author
      this.$router.push({
        path: '/author-articles',
        query: { authorId: author.id, authorName: author.name }
      })
    },
    
    // 查看文章详情
    viewArticle(article) {
      this.$message({
        message: `查看《${article.title}》详情`,
        type: 'info'
      })
      // 实际项目中这里会跳转到文章详情页
    },
    
    // 删除文章
    deleteArticle(article) {
      this.$confirm(`确认删除文章《${article.title}》?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟删除操作
        const index = this.articles.findIndex(item => item.id === article.id)
        if (index !== -1) {
          this.articles.splice(index, 1)
          
          // 更新文章计数
          if (this.selectedAuthor) {
            const authorIndex = this.authorList.findIndex(author => author.id === this.selectedAuthor.id)
            if (authorIndex !== -1 && this.authorList[authorIndex].articleCount > 0) {
              this.authorList[authorIndex].articleCount--
              this.selectedAuthor.articleCount--
            }
          }
          
          // 更新图表和当前显示的文章列表
          this.authorArticles = this.articles.filter(article => article.authorId === this.selectedAuthor.id)
          this.updateChart()
          
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    }
  }
}
</script>

<style scoped>
.article-manager-container {
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

.article-manager {
  padding-bottom: 20px;
}

.article-manager h1 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.el-card {
  margin-bottom: 20px;
}

::v-deep .selected-row {
  background-color: #f0f9eb;
}
</style> 