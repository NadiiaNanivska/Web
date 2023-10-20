import {BrowserRouter, Route, Routes} from 'react-router-dom';
import './App.css';
import SignUp from './SignUp/SignUp';
import SignIn from './SignIn/SignIn';
import HomePage from './Home/Home';
import { ConfigProvider } from 'antd';

function App() {
  return (
    <ConfigProvider
            theme={{
                token: {
                    colorPrimary: '#640D14',
                    colorText: '#38040E',
                    colorTextPlaceholder: '#C9B0B2',
                    colorBorder: '#C9B0B2',
                    colorIcon: '#C9B0B2',
                    colorLink: '#AD2831',
                    controlHeightLG: 60
                }
            }}
        >
    <div className="App">
    <BrowserRouter>
         <Routes>
             <Route path="/signin" element={<SignIn />}></Route>
             <Route path="/signup" element={<SignUp />}></Route>
             <Route path="/" element={<HomePage />}></Route>
         </Routes>
       </BrowserRouter>
    </div>
    </ConfigProvider>
  );
}

export default App;
