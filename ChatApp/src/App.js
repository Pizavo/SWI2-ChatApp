import './App.css';
import {useState} from "react";
import LoginPage from "./Components/LoginPage";
import MenuPage from "./Components/MenuPage";

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
        <div className="App">
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Display:wght@300;400;600&display=swap');
            </style>
            {!user ? (
                <LoginPage setUserToken={setUserToken}/>
            ) : (
                <MenuPage username={user} setUserToken={setUserToken}/>
            )}
        </div>
    );
}

export default App;
