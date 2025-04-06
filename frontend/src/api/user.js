import axios from 'axios'

export const getUserProfile = () => {
  return axios.get('/users/profile')
}

export const updateUserProfile = (userData) => {
  // 使用FormData来处理文件上传
  const formData = new FormData()
  
  if (userData.email) formData.append('email', userData.email)
  if (userData.birthday) formData.append('birthday', userData.birthday)
  if (userData.avatar) formData.append('avatar', userData.avatar)
  
  return axios.put('/users/profile', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const deleteUser = (password) => {
  return axios.delete('/users/profile', {
    data: { password }
  })
} 