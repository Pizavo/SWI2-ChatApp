import React, { useEffect, useState } from 'react';
import { Button, TextField, Box, Alert, Divider, Chip, Modal, Typography, IconButton, Backdrop, Fade } from '@mui/material';
import { AccountCircle, LockOpen, Close } from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import LoginIcon from '@mui/icons-material/Login';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const SIGNUP_URL = 'http://localhost:8081/api/signup';

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
    background: 'linear-gradient(45deg, #33aa22, #00ff66)',
    color: 'black',
});

const SignUpButton = styled(Button)({
    transition: 'all .2s ease-in-out',
    background: '#18181a',
    color: '#b9bbbd',
    borderRadius: '20px',
    ":hover": {
        background: '#0e0e0f',
        color: 'white',
        transform: 'scale(1.1)'
    },
});

const style = {
    position: 'absolute',
    top: '35%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '60%',
    bgcolor: '#1f1f22',
    border: '2px solid #000',
    p: 2,
    color: 'white'
};

const Signup = (props) => {

    const [errorMessage, setErrorMessage] = useState("");
    const [registrationSuccessful, setRegistrationSuccessful] = useState(false);
    const [countdown, setCountdown] = useState(0);

    useEffect(() => {
        countdown > 0 && setTimeout(() => setCountdown(countdown - 1), 1000);
    }, [countdown]);

    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [validUsername, setValidUsername] = useState(true);
    const [validPassword, setValidPassword] = useState(true);

    const [loginButtonClicked, setLoginButtonClicked] = useState(false);

    useEffect(() => {
        setValidUsername(username.length > 0);
        setValidPassword(password.length > 0);
    }, [username, password]);

    function signup(e) {
        e.preventDefault();
        setLoginButtonClicked(true);
        if (!validUsername || !validPassword) {
            return;
        }

        const signupBody = {
            username: username,
            password: password
        };

        axios.post(SIGNUP_URL, signupBody)
            .then(response => {
                //setUsername(response.data);
                setRegistrationSuccessful(true);
                setCountdown(3);
                setTimeout(() => {
                    handleClose();
                }, 2800);
                setTimeout(() => {
                    navigate('/');
                }, 3000);
                handleOpen();
            })
            .catch(error => {
                setErrorMessage(error.response.data);
                setRegistrationSuccessful(false);
                handleOpen();
            });
    }

    return (
        <div className='App'>
            <Modal
                open={open}
                onClose={handleClose}
                aria-describedby="modal-modal-description"
                closeAfterTransition
                BackdropComponent={Backdrop}
                BackdropProps={{ timeout: 500 }}
            >
                <Fade in={open}>
                    <Box sx={style}>
                        <IconButton
                            onClick={handleClose}
                            sx={{ position: 'absolute', left: '1', right: '0', top: '0', bottom: '1', color: 'white' }}
                            style={{ backgroundColor: 'transparent' }}
                        >
                            <Close></Close>
                        </IconButton>
                        {registrationSuccessful ? (
                            <div>
                                <Alert severity='success' variant='string' sx={{ color: '#00ff66', justifyContent: 'center', fontSize: '100%' }}>Registration successful!</Alert>
                                <Typography id="modal-modal-description" sx={{ mt: 1, textAlign: 'center' }}>
                                    Redirecting to login page in {countdown}s
                                </Typography>
                            </div>
                        ) : (
                            <div>
                                <Alert severity='error' variant='string' sx={{ color: '#d64242', justifyContent: 'center', fontSize: '100%' }}>{errorMessage}</Alert>
                            </div>
                        )}
                    </Box>
                </Fade>

            </Modal>
            <div>
                <h2 className='MyFont'>Please Sign up</h2>
                <form onSubmit={signup}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <AccountCircle sx={{ color: 'white', mr: 1, my: 0.5 }} />
                        <CssTextField sx={{ fontFamily: 'Play' }} label='Username' onChange={e => setUsername(e.target.value)} />
                    </Box>
                    {!validUsername && loginButtonClicked ? (
                        <Alert variant='string' severity='error' sx={{ color: '#d64242', marginLeft: '15px' }}>Required</Alert>
                    ) : (
                        <Box sx={{ marginBottom: '15px' }}></Box>
                    )}
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LockOpen sx={{ color: 'white', mr: 1, my: 0.5 }} />
                        <CssTextField label='Password' type='password' onChange={e => setPassword(e.target.value)} />
                    </Box>
                    {!validPassword && loginButtonClicked ? (
                        <Alert variant='string' severity='error' sx={{ color: '#d64242', marginLeft: '15px' }}>Required</Alert>
                    ) : (
                        <Box sx={{ marginBottom: '15px' }}></Box>
                    )}
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <LoginIcon sx={{ color: 'white', mr: 1, my: 0.5 }} />
                        <LoginButton
                            disabled={registrationSuccessful}
                            type='submit'
                            variant='contained'
                            sx={{
                                borderRadius: 2,
                                width: 1
                            }}
                        ><div className='MyFont'>Sign up</div></LoginButton>
                    </Box>
                </form>
                <Divider sx={{ marginTop: '15px', marginBottom: '15px', borderBottomWidth: '50px' }}>
                    <Chip label="OR" variant='plain' sx={{ color: 'white' }} />
                </Divider>
                <Link to="/" style={{ textDecoration: 'none' }}>
                    <SignUpButton>
                        <div className='MyFont'>Log in</div>
                    </SignUpButton>
                </Link>
            </div>
        </div>
    )
}

export default Signup