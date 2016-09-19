function getCookieValueX(cookieName){
	try{
		return cookie.get(cookieName).getValue();
	}catch(e){
		return "";
	}
}  