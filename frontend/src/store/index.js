import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import { register as apiRegister } from '@/api/auth'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || null
  },
  getters: {
    isAuthenticated: state => !!state.token,
    user: state => state.user
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_USER(state, user) {
      state.user = user
    },
    LOGOUT(state) {
      state.token = ''
      state.user = null
    }
  },
  actions: {
    async login({ commit }, user) {
      try {
        const res = await axios.post('/auth/login', user)
        const token = res.data.token
        const userData = res.data.user
        
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(userData))
        
        commit('SET_TOKEN', token)
        commit('SET_USER', userData)
        
        return res
      } catch (error) {
        throw error
      }
    },
    
    async register({ commit }, userData) {
      try {
        const res = await apiRegister(userData)
        return res
      } catch (error) {
        throw error
      }
    },
    
    logout({ commit }) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      commit('LOGOUT')
    },
    
    async updateProfile({ commit }, userData) {
      try {
        const res = await axios.put('/users/profile', userData)
        const updatedUser = res.data
        
        localStorage.setItem('user', JSON.stringify(updatedUser))
        commit('SET_USER', updatedUser)
        
        return res
      } catch (error) {
        throw error
      }
    }
  }
}) 