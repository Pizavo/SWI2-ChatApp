import './LoginPage.css';
import React, {useEffect, useState} from "react";
import {Box, Button, TextField} from "@mui/material";
import {styled} from "@mui/material/styles";
import axios from "axios";

const LOGIN_TOKEN_URL = 'http://localhost:8081/login';
const SIGNUP_URL = 'http://localhost:8081/api/signup';

const LoginButton = styled(Button)({
    backgroundColor: 'white',
    color: 'black',
    borderRadius: '25px'
});

const LoginTextField = styled(TextField)({
    backgroundColor: 'white',
    color: 'black',
    borderRadius: '50px',
    width: '400px'
})

const LoginPage = (props) => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [validUsername, setValidUsername] = useState(true);
    const [validPassword, setValidPassword] = useState(true);

    const [loginButtonClicked, setLoginButtonClicked] = useState(false);

    const [responseMessage, setResponseMessage] = useState("");
    const [loginError, setLoginError] = useState("");

    useEffect(() => {
        setValidUsername(username.length > 0);
        setValidPassword(password.length > 0);
    }, [username, password]);

    function loginUser(e) {
        e.preventDefault();
        setLoginButtonClicked(true);
        if (!validUsername || !validPassword) {
            return;
        }
        // volani backendu - kontrola do databáze, zda existuje, kontrola hesla

        const loginBody = {
            username: username,
            password: password
        };

        axios.post(LOGIN_TOKEN_URL, loginBody)
            .then(response => {
                props.setUserToken(response.data);
            })
            .catch(error => {
                try {
                    setLoginError(error.response.data);
                } catch (e) {
                    setLoginError("ERROR: Cannot access authentication service!");
                }
                setLoginButtonClicked(false);
            })
    }

    function signupUser(e) {
        //e.preventDefault();
        // volani backendu
        const signupBody = {
            username: username,
            password: password
        };

        axios.post(SIGNUP_URL, signupBody)
            .then(response => {
                setResponseMessage(response.data);
            })
            .catch(error => {
                try {
                    setResponseMessage(error.response.data);
                } catch (e) {
                    setResponseMessage("ERROR: Cannot access registration service!");
                }
            })
    }

    return (
        <div className="LoginPage">
            <h2>Please log in</h2>
            <form onSubmit={loginUser}>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <LoginTextField
                        required
                        label='Username'
                        onChange={e => setUsername(e.target.value)}
                    />
                </Box>
                <Box sx={{display: 'flex', alignItems: 'center', marginTop: '15px', marginBottom: '15px'}}>
                    <LoginTextField
                        required
                        label='Password'
                        type='password'
                        onChange={e => setPassword(e.target.value)}
                    />
                </Box>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <LoginButton
                        type='submit'
                        sx={{
                            boarderRadius: 2,
                            width: 1
                        }}
                    >
                        Log in
                    </LoginButton>
                </Box>
            </form>
            <Box sx={{display: 'flex', alignItems: 'center', marginTop: '15px'}}>
                <LoginButton
                    onClick={signupUser}
                    sx={{
                        boarderRadius: 2,
                        width: 1
                    }}>
                    Sign up
                </LoginButton>
            </Box>
            {responseMessage}
        </div>
    )
}

export default LoginPage