import jwtDecode from "jwt-decode";
export const BASE_URL = 'http://localhost:8081/api';

const MAX_VALUE = 9223372036854775807n;

export const validatePasswordMatch = ({ getFieldValue }) => ({
  validator(_, value) {
    if (!value || getFieldValue('password') === value) {
      return Promise.resolve();
    }
    return Promise.reject(new Error('The new password that you entered do not match!'));
  },
});


export const validatePassword = () => ({
  validator(_, value) {
    if (value.length < 8 || value.length > 20) {
      return Promise.reject(new Error('Password must be between 8 and 20 characters'));
    }

    if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).*$/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one uppercase and lowercase letter'));
    }

    if (!/^(?=.*\d).*$/.test(value)) {
      return Promise.reject(new Error('Password must contain at least one number'));
    }

    if (!/^[^А-Яа-яЇїІіЄєҐґЁё]+$/.test(value)) {
      return Promise.reject(new Error('Password must contain only Latin letters'));
    }

    return Promise.resolve();
  }
});


export const validateInputNumber = () => ({
  validator(_, value) {
    if (value > MAX_VALUE) {
      return Promise.reject(new Error(`The input number must be less than ${MAX_VALUE}!`));
    }
    return Promise.resolve();
  },
});

export function getErrorMessage(response) {
  if (response && typeof response === 'object') {
    const errorFields = Object.keys(response);
    if (errorFields.length > 0) {
      return response[errorFields[0]];
    }
  }
  return null;
}

export function isTokenValid(token) {
  if (!token) return false;
  try {
    const decodedToken = jwtDecode(token);
    const currentTime = Math.floor(Date.now() / 1000);
    return decodedToken.exp > currentTime;
  } catch (error) {
    return false;
  }
};