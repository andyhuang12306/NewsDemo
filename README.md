# NewsDemo

About Structure:  MVVM+rxJava+Retrofit+OKHttp+DataBinding

About Cache: okhttp client set with internet interceptor to change the request header and response header with cache control

About List: recyclerview with ItemTouchlistener to handle the left scroll to delete and view onclicklistener to handle click

About Pagination: recyclerview with OnScrollListener to ask viewmodel to load next page once the last item is visible

About Screen: one activity to container two fragments which with one to list All News and ReadAlreadyNews at the same, with another to display the detail of one specific news