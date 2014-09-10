package note.api;

public class ApiException extends Exception {

	public enum Error {
		ERROR_CONNECTION, ERROR_SERVER, ERROR_JSON, ERROR
	};

	final Error typeOfError;

	public Error getError() {
		return typeOfError;
	}

	public ApiException(Error tOfEr, Throwable throwable) {
		super(throwable);
		typeOfError = tOfEr;
	}
}