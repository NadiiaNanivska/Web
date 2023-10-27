import React from 'react';
import { Form, Input, Button, Typography, message, notification } from 'antd';
import { Link, useNavigate } from 'react-router-dom';
import { getErrorMessage, validatePassword, validateEmail } from '../Utils';
import { loginUser } from '../Requests/AuthenticationRequests';
import './SignIn.css';

const { Title } = Typography;

function SignIn() {
  const history = useNavigate();
  const [api, contextHolder] = notification.useNotification();
  const [messageApi, msgcontextHolder] = message.useMessage();
  const openNotificationWithIcon = (type, message, description) => {
    api[type]({
      message: message,
      description: description,
    });
  };
  const onFinish = async (values) => {
      const response = await loginUser(values);
      if (response.accessToken) {
        messageApi.open({
          type: 'success',
          content: 'Sign in successful',
        });
        localStorage.setItem('accessToken', response.accessToken);
        localStorage.setItem('refreshToken', response.refreshToken);
        setTimeout(() => {
          history('/');
        }, 1000);
      } else {
        const errorMessage = getErrorMessage(response);
        if (errorMessage) {
          openNotificationWithIcon('error', 'Error during sign in', errorMessage);
        }
      }
  };

  return (
    <div className="signin-container">
      {contextHolder}
      {msgcontextHolder}
      <Form
        name="registration"
        onFinish={onFinish}
        labelCol={{ span: 6 }}
        className="signin-form"
      >
        <Title className="signin-title">Sign in</Title>
        <Form.Item
          name="email"
          rules={[
            {
              required: true,
              message: 'Please input your email!',
            },
            validateEmail
          ]}
        >
          <Input type="email" placeholder="Email" />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[
            {
              required: true,
              message: 'Please input your password!',
            },
            validatePassword
          ]}
        >
          <Input.Password placeholder="Password" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" className="signin-button" htmlType="submit"> 
            Sign in
          </Button>
          <p> 
            Don't have an account?{' '}
            <Link to="/signup">Sign up</Link>
          </p>
        </Form.Item>
      </Form>
    </div>
  );
}

export default SignIn;