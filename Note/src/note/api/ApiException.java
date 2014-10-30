package note.api;

public class ApiException extends Exception {

	public enum TypeOfError {
		ERROR_CONNECTION, ERROR_PARSE_RESPONSE, ERROR_WRONG_DATA
	};

	static TypeOfError	typeOfError;

	public TypeOfError getError(){
		return typeOfError;
	}

	public ApiException(TypeOfError typeOfError,Throwable throwable) {
		super(throwable);
		this.typeOfError = typeOfError;
	}
	
}