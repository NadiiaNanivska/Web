import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Typography, Result, FloatButton, Progress, message } from 'antd';
import TaskHistory from '../TaskHistory/TaskHistory';
import './Home.css';
import { cancelTask, createTask } from '../Requests/TaskRequests';
import { isTokenValid, validateInputNumber } from '../Utils';
import { useNavigate } from 'react-router-dom';
import { LogoutOutlined } from '@ant-design/icons';
const { v4: uuidv4 } = require('uuid');

const { Title } = Typography;
let socket;

function HomePage() {
    const history = useNavigate();
    const [inputValue, setInputValue] = useState('');
    const [result, setResult] = useState(null);
    const [uniqueId, setUniqueId] = useState('');
    const YOUR_LIMIT = 5;
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [messageApi, msgcontextHolder] = message.useMessage();
    const [taskProgress, setTaskProgress] = useState(0);
    const [isProgressBarVisible, setProgressBarVisible] = useState(false); 
    const [activeTaskCount, setActiveTaskCount] = useState(0);


    useEffect(() => {
        const storedToken = localStorage.getItem('refreshToken');
        if (!isTokenValid(storedToken)) {
            localStorage.removeItem('refreshToken');
            history('/signin');
        }
    }, []);

    const showModal = () => {
        setIsModalVisible(true);
    };

    const closeModal = () => {
        setIsModalVisible(false);
    };

    const onFinish = async (values) => {
        if (activeTaskCount >= YOUR_LIMIT) {
            messageApi.open({
                type: 'warning',
                content: `Max quantity of tasks is ${YOUR_LIMIT}`,
              });
            return;
          }
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.close();
        }
        setActiveTaskCount(activeTaskCount + 1);
        const newUniqueId = uuidv4();
        setUniqueId(newUniqueId);
        socket = new WebSocket(`ws://localhost:8081/ws-endpoint?taskId=${newUniqueId}`);
        setProgressBarVisible(true);
        try {
            socket.onmessage = (event) => {
                const messageData = parseInt(event.data);
                setTaskProgress(messageData);
            };
            const result = await createTask(values, newUniqueId);
            if(result.status === 201) {
                setResult(`Calculated successfully: Factors of ${result.data.inputValue} are ${result.data.firstFactor} and ${result.data.secondFactor}`);
            }
            setActiveTaskCount(activeTaskCount - 1);
        } catch (error) {
            socket.close();
        }
        setTaskProgress(0);
        setProgressBarVisible(false);
    };

    const handleCancelTask = async () => {
        try {
            const data = await cancelTask(uniqueId);
            if(data.status === 200) {
                socket.close();
                setActiveTaskCount(activeTaskCount - 1);
            }
            setTaskProgress(0);
            setProgressBarVisible(false);
            setResult('Task is cancelled');
        } catch (error) {
            setResult('An error occurred');
        }
    };

    const handleLogout = () => {
       localStorage.removeItem('token');
       history('/signin');
    };

    return (
        <div>
            {msgcontextHolder}
            <Title className="home-title">Find the Two Largest Prime Factors</Title>
            <div className="home-container">
                <Form onFinish={onFinish} className="home-form">
                    <Form.Item
                        name="inputValue"
                        rules={[
                            {
                                required: true,
                                message: 'Please input a number!',
                            },
                            validateInputNumber
                        ]}
                    >
                        <Input
                            placeholder="Enter a number"
                            type="number"
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                        />
                    </Form.Item>
                    {isProgressBarVisible && <Progress percent={taskProgress} />}
                    <Form.Item>
                        <Button type="primary" htmlType="submit" className="home-buttons">
                            Calculate
                        </Button>
                        <Button type="default" onClick={handleCancelTask} className="home-buttons">
                            Cancel Task
                        </Button>
                    </Form.Item>
                </Form>
                <TaskHistory visible={isModalVisible} onClose={closeModal} />
            </div>
            <Button onClick={showModal}>View Task History</Button>
            <FloatButton type="primary" tooltip={<div>Log out</div>} icon={<LogoutOutlined />} onClick={handleLogout} />
            {result !== null && (
                <Result
                    status={result === 'Task is cancelled' || result.startsWith('Calculated successfully') ? 'success' : 'error'}
                    title={
                        result === 'Task is cancelled'
                            ? 'Task is successfully canceled'
                            : result.startsWith('Calculated successfully')
                            ? 'Calculation Succeeded'
                            : 'An Error Occurred'
                    }
                    subTitle={result}
                />
            )}
        </div>
    );
}

export default HomePage;
