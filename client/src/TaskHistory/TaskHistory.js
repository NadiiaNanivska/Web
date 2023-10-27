import React, { useState, useEffect } from 'react';
import { Button, Modal, List, Col, Row, message } from 'antd';
import { getTaskHistory } from '../Requests/TaskRequests';

function TaskHistory({ visible, onClose }) {
    const [taskHistory, setTaskHistory] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchTaskHistory = async () => {
        try {
            setLoading(true);
            const data = await getTaskHistory();
            setTaskHistory(data);
        } catch (error) {
            message.open({
                type: 'error',
                content: "An error occured"
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (visible) {
            fetchTaskHistory();
        }
    }, [visible]);

    return (
        <Modal
            width={'50rem'}
            title="Task History"
            visible={visible}
            onOk={onClose}
            onCancel={onClose}
            footer={[
                <Button key="refresh" onClick={fetchTaskHistory} loading={loading}>
                    Refresh Task History
                </Button>
            ]}
        >
            <List
                dataSource={taskHistory}
                renderItem={(item) => (
                    <List.Item>
                        <Col span={8}>
                            {item.inputValue}
                        </Col>
                        <Col span={8}>
                            {item.firstFactor}
                        </Col>
                        <Col span={8}>
                            {item.secondFactor}
                        </Col>
                    </List.Item>
                )}
                header={
                    <Row>
                        <Col span={8}>Input Value</Col>
                        <Col span={8}>First Factor</Col>
                        <Col span={8}>Second Factor</Col>
                    </Row>
                }
            />
        </Modal>
    );
}

export default TaskHistory;
