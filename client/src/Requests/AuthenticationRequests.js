import axios from 'axios';
import { BASE_URL } from '../Utils';

export const registerUser = async (registrationData) => {
  try {
    const response = await axios.post(`${BASE_URL}/auth/signup`, registrationData);
    return response.data; 
  } catch (error) {
    if (error.response) {
      return error.response.data; 
    }
  }
};

export const loginUser = async (loginData) => {
    try {
      const response = await axios.post(`${BASE_URL}/auth/signin`, loginData);
      return response.data; 
    } catch (error) {
      if (error.response) {
        return error.response.data; 
      }
    }
  };