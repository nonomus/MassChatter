package mainPackage;

public enum MYSQL_ACCESS_TYPE {
	LOGIN,
	LOGIN_SUCCESS,
	LOGIN_ERROR_USERNAME_NOT_FOUND,
	LOGIN_ERROR_INCORRECT_PASSWORD,
	
	REGISTER,
	REGISTER_SUCCESS,
	REGISTER_ERROR_USERNAME_ALREADY_EXISTS,
	
	
	ERROR;
}