import React from 'react';
import { Form, Input, Button, Typography, notification, message } from 'antd';
import { Link, useNavigate } from 'react-router-dom';
import {validatePasswordMatch, getErrorMessage} from '../Utils';
import {registerUser} from '../Requests/AuthenticationRequests';
import './SignUp.css';
 
const { Title } = Typography;

function SignUp() {
  const history = useNavigate();
  const [api, contextHolder] = notification.useNotification();
  const [messageApi, msgcontextHolder] = message.useMessage();
  const openNotificationWithIcon = (type, message, description) => {
    api[type]({
      message: message,
      description:
      description,
    });
  };
  const onFinish = async (values) => {
    const response = await registerUser(values);
    if (response.accessToken) {
      messageApi.open({
        type: 'success',
        content: 'Registration successful',
      });
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      setTimeout(() => {
        history('/');
      }, 500);
    } else {
      const errorMessage = getErrorMessage(response);
      if (errorMessage) {
      openNotificationWithIcon('error','Error during registration', errorMessage);
      }
    }
};

  return (
    <div className="signup-container">
      {contextHolder}
      {msgcontextHolder}
      <Form
      className="signup-form"
        name="registration"
        onFinish={onFinish}
        labelCol={{ span: 6 }}
      >
        <Title className="signup-title">Sign up</Title>
        <Form.Item
          name="email"
          rules={[
            {
              required: true,
              message: 'Please input your email!',
            },
          ]}
        >
          <Input type="email" placeholder="Email"/>
        </Form.Item>
        <Form.Item
          name="password"
          dependencies={['confirmPassword']}
          rules={[
            {
              required: true,
              message: 'Please input your password!',
            },
          ]}        
        >
          <Input.Password placeholder="Password"/>
        </Form.Item>
        <Form.Item
          name="confirmPassword"
          dependencies={['password']}
          rules={[
            {
              required: true,
              message: 'Please input confirm password!',
            },
            validatePasswordMatch,
          ]}
        >
          <Input.Password placeholder="Confirm password"/>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Sign up
          </Button>
        </Form.Item>
        <p>
        Already have an account?{' '}
        <Link to="/signin">Sign in</Link>
      </p>
      </Form>
    </div>
  );
}

export default SignUp;
