import './App.css';
import Home from './components/Home';
import Signup from './components/Signup';
import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';
import Navbar from './components/Navbar';
import TryChat from './components/TryChat';

function App() {

  const [user, setUser] = useState(getUserToken());

  function setUserToken(userToken) {
    localStorage.setItem('userToken', JSON.stringify(userToken));
    setUser(userToken);
  }

  function getUserToken() {
    const userTokenStr = localStorage.getItem('userToken');

    let userToken = "";
    try {
      userToken = JSON.parse(userTokenStr);
    } catch (e) {
      console.log(e.message);
    }

    return userToken;
  }

  return (
    <div>
      <Navbar user={user} setUserToken={setUserToken} />
      <Routes>
        <Route path='/' element={<Home user={user} setUserToken={setUserToken} />} />
        <Route path='/signup' element={<Signup />} />
        <Route path='/trying' element={<TryChat />} />
      </Routes>
    </div>
  );
}

export default App;
