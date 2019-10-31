package platform.util.exception;

public class GetElementException extends Exception {
	public String url;
	public String type;

	public GetElementException(String _url, String _type) {
		super(_type+" ["+_url+"]");
		url = _url;
		type = _type;
	}
}
