import React from 'react'
import Login from './Login'
import MainPage from './MainPage'

const Home = (props) => {
	
	return (
		<div className="App">
			{!props.user ? (
				<Login setUserToken={props.setUserToken} unsetUserToken={props.unsetUserToken}/>
			) : (
				<MainPage user={props.user}/>
			)}
		</div>
	)
}

export default Home