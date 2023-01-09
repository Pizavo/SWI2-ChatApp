import React, { useEffect, useState } from 'react';
import { Button, TextField, Box, Alert, Divider, Chip } from '@mui/material';
import { AccountCircle, LockOpen } from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import LoginIcon from '@mui/icons-material/Login';
import { Link } from 'react-router-dom';
import axios from 'axios';

const LOGIN_TOKEN_URL = 'http://localhost:8081/login';

const CssTextField = styled(TextField)({
  '& label.Mui-focused': {
    color: 'white',
  },
  '& label': {
    color: 'white',
  },
  '& .MuiInput-underline:after': {
    borderBottomColor: 'green',
  },
  '& input': {
    color: 'white',
  },
  '& .MuiOutlinedInput-root': {
    '& fieldset': {
      borderColor: 'grey',
    },
    '&:hover fieldset': {
      borderColor: 'white',
    },
    '&.Mui-focused fieldset': {
      borderColor: 'white',
    },
  },
});

const LoginButton = styled(Button)({
  //background: 'linear-gradient(45deg, #33aa22, #00ff66)',
  background: '#9c49f3',
  color: 'black',
  fontWeight: 'bold'
});

const SignUpButton = styled(Button)({
  transition: 'all .2s ease-in-out',
  background: '#18181a',
  color: '#b9bbbd',
  borderRadius: '20px',
  ":hover": {
    background: '#0e0e0f',
    color: 'white',
    transform: 'scale(1.1)',
  },
});

const Login = (props) => {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [validUsername, setValidUsername] = useState(true);
  const [validPassword, setValidPassword] = useState(true);

  const [loginButtonClicked, setLoginButtonClicked] = useState(false);

  const [loginError, setLoginError] = useState("");

  useEffect(() => {
    setValidUsername(username.length > 0);
    setValidPassword(password.length > 0);
  }, [username, password]);

  function login(e) {
    e.preventDefault();
    setLoginButtonClicked(true);
    if (!validUsername || !validPassword) {
      return;
    }
    // Volani backendu pro kontrolu jmena a hesla

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
        } catch(e) {
          setLoginError("Cannot access authentication server!");
        }
        setLoginButtonClicked(false);
      });
  }

  return (
    <div>
      <h2 className='MyFont'>Please Log in</h2>
      <form onSubmit={login}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <AccountCircle sx={{ color: 'white', mr: 1, my: 0.5 }} />
          <CssTextField sx={{ fontFamily: 'Play' }} label='Username' onChange={e => setUsername(e.target.value)} />
        </Box>
        {!validUsername && loginButtonClicked ? (
          <Alert variant='string' severity='error' sx={{ color: '#d64242', marginLeft: '15px' }}>Required</Alert>
        ) : (
          <Box sx={{ marginBottom: '15px' }}></Box>
        )}
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <LockOpen sx={{ color: 'white', mr: 1, my: 0.5 }} />
          <CssTextField label='Password' type='password' onChange={e => setPassword(e.target.value)} />
        </Box>
        {!validPassword && loginButtonClicked ? (
          <Alert variant='string' severity='error' sx={{ color: '#d64242', marginLeft: '15px' }}>Required</Alert>
        ) : (
          <Box sx={{ marginBottom: '15px' }}></Box>
        )}
        {loginError ? (
          <Alert variant='string' severity='error' sx={{ color: '#d64242', marginLeft: '15px', marginTop: '-15px' }}>{loginError}</Alert>
        ) : (
          <></>
        )}
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <LoginIcon sx={{ color: 'white', mr: 1, my: 0.5 }} />
          <LoginButton
            type='submit'
            variant='contained'
            sx={{
              borderRadius: 2,
              width: 1
            }}
          ><div className='MyFont'>Log in</div></LoginButton>
        </Box>
      </form>
      <Divider sx={{ marginTop: '15px', marginBottom: '15px', borderBottomWidth: '50px' }}>
        <Chip label="OR" variant='plain' sx={{ color: 'white' }} />
      </Divider>
      <Link to="/signup" style={{ textDecoration: 'none' }}>
        <SignUpButton>
          <div className='MyFont'>Sign up now</div>
        </SignUpButton>
      </Link>
    </div>
  )
}

export default Login