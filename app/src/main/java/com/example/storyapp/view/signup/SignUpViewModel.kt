import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.repository.SignUpRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpViewModel(private val repository: SignUpRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(name, email, password)
                if (response.error == false) {
                    _registerResult.postValue(Result.success(response.message ?: "Registrasi berhasil"))
                } else {
                    _registerResult.postValue(Result.failure(Throwable(response.message)))
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                _registerResult.value = Result.failure(Throwable(errorMessage))
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }

        }
    }
}

