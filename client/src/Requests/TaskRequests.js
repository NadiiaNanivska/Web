import axios from 'axios';
import { BASE_URL } from '../Utils';

export const createTask = async (taskRequest, uniqueId) => {
    const response = await axios.post(`${BASE_URL}/tasks?taskId=${uniqueId}`, taskRequest, {
      headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }});
      return response.data;
};

export const getTaskHistory = async () => {
    const response = await axios.get(`${BASE_URL}/tasks/history`,  {
    headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`, 
      }});
    if (response.status === 200) {
      return response.data;
    } else {
      throw new Error('An error occurred');
    }
};

export const cancelTask = async (uniqueId) => {
    const response = await axios.delete(`${BASE_URL}/tasks/cancel?taskId=${uniqueId}`,  {
      headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }});
      return {'data': response.data, 'status': response.status};
};


axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshResponse = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken: localStorage.getItem('refreshToken'),
        });
        if (refreshResponse.status === 200) {
          localStorage.setItem('accessToken', refreshResponse.data.accessToken);
          originalRequest.headers['Authorization'] = `Bearer ${refreshResponse.data.accessToken}`;
          return axios(originalRequest);
        }
      } catch (refreshError) {
      }
    }

    return Promise.reject(error);
  }
);