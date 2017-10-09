package kz.worldclass.finances.data.dto.results.authmanage;

public class SaveUserException extends RuntimeException {
    private final SaveUserResult result;
    
    public SaveUserException(SaveUserResult result) {
        this.result = result;
    }

    public SaveUserResult getResult() {
        return result;
    }
}