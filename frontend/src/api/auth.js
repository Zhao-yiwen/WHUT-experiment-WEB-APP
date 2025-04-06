import axios from 'axios'

export const login = (username, password) => {
  return axios.post('/auth/login', { username, password })
}

export const register = (userData) => {
  // 使用FormData来处理文件上传
  const formData = new FormData()
  formData.append('username', userData.username)
  formData.append('password', userData.password)
  formData.append('email', userData.email)
  
  // 确保日期格式正确
  if (userData.birthday) {
    // 如果是Date对象，转换为字符串
    const birthday = typeof userData.birthday === 'object' 
      ? userData.birthday.toISOString().split('T')[0] 
      : userData.birthday
    formData.append('birthday', birthday)
  }
  
  if (userData.avatar) {
    formData.append('avatar', userData.avatar)
  }
  
  // 调试输出
  console.log('发送的表单数据:')
  for (let pair of formData.entries()) {
    console.log(pair[0] + ': ' + pair[1])
  }
  
  return axios.post('/auth/register', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const updatePassword = (oldPassword, newPassword) => {
  return axios.put('/auth/password', { oldPassword, newPassword })
}

export const logout = () => {
  return axios.post('/auth/logout')
} 