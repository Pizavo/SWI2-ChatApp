import React, { useState } from 'react';
import Login from './Login';
import MainPage from './MainPage';

const Home = (props) => {

    return (
        <div className="App">
            {!props.user ? (
                <Login setUserToken={props.setUserToken} />
            ) : (
                <MainPage user={props.user} />
            )}
        </div>
    )
}

export default Home