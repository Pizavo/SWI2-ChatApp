import React from 'react'
import {AppBar, Avatar, IconButton, Stack, Toolbar, Tooltip, Typography, useMediaQuery, useTheme} from '@mui/material'
import {styled} from '@mui/material/styles'
import TelegramIcon from '@mui/icons-material/Telegram'
import LogoutIcon from '@mui/icons-material/Logout'

const LogoutText = styled(Typography)({
	transition: 'background 0.3s, color 0.3s',
	':hover': {
		background: 'transparent',
	},
	paddingLeft: '2px',
})

const LogoutIconButton = styled(IconButton)({
	color: '#999b9d',
	'&:hover': {
		color: 'white',
		background: 'transparent',
	},
})

const Navbar = (props) => {
	const theme = useTheme()
	const isSmallRes = useMediaQuery(theme.breakpoints.down('sm'))
	
	function logout() {
		props.unsetUserToken()
	}
	
	return (
		<AppBar position="static" sx={{background: '#18181a', borderBottom: 1, borderColor: '#999b9d'}} elevation={0}>
			<Toolbar>
				<IconButton size="large" edge="start" color="inherit" sx={{cursor: 'default'}}>
					<TelegramIcon sx={{color: '#9c49f3'}}/>
				</IconButton>
				{isSmallRes ? (
					<></>
				) : (
					<Typography variant="h6" component="div">
						<div className="MyFont">HelloThere</div>
					</Typography>
				)}
				{props.user ? (
					<Stack direction="row" spacing={2} marginLeft="auto" alignItems="center">
						<Tooltip title={
							<React.Fragment>
								{"Now logged in: "}<b style={{color: '#d3abff'}}>{props.user.username}</b>
							</React.Fragment>
						} placement="left" arrow>
							<Avatar sx={{bgcolor: '#9c49f3', color: 'black'}}>
								<div className="MyFont">{props.user.username.charAt(0).toUpperCase()}</div>
							</Avatar>
						</Tooltip>
						<LogoutIconButton color="inherit" onClick={logout}>
							<LogoutIcon color="inherit" sx={{transition: 'background 0.3s, color 0.3s'}}/>
							<LogoutText variant="button">
								<div className="MyFont">Log out</div>
							</LogoutText>
						</LogoutIconButton>
					</Stack>
				) : (
					<div></div>
				)}
			</Toolbar>
		</AppBar>
	)
}

export default Navbar