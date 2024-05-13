package com.t3h.networking

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.t3h.networking.model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)
        thread(start = true) {
            val sBuilder = StringBuilder()

            try {
                val url = URL("https://api.github.com/users")
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 60000

                val br = BufferedReader(InputStreamReader(conn.inputStream))

                var str: String?

                while (br.readLine().also { str = it } != null) {
                    if (str.isNullOrEmpty().not()) {
                        sBuilder.append(str)
                    }
                }
            } catch (ex: Exception) {
                Log.d("3igZeus", "Ex is call : $ex")
            }
//            Log.d("3igZeus", "first = ${sBuilder.toString().firstOrNull()}")
//            Log.d("3igZeus", "last = ${sBuilder.toString().lastOrNull()}")
//
//            val result = sBuilder.toString().substring(1, sBuilder.length - 1)
//
//            Log.d("3igZeus", "first = ${result.firstOrNull()}")
//            Log.d("3igZeus", "last = ${result.lastOrNull()}")
//
//            val arrayModel = result.split("},")
//            Log.d("3igZeus", "arr size = ${arrayModel.size}")
            // xoá bỏ ngoặc ở trước và sau.
            // TODO:
            val users = arrayListOf<User>()

            val jsonArray = JSONArray(sBuilder.toString())

            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                val user = User().apply {
                    if (jsonObj.has("login")) {
                        user = jsonObj.getString("login")
                    }
                    if (jsonObj.has("id")) {
                        id = jsonObj.getInt("id")
                    }
                    if (jsonObj.has("node_id")) {
                        nodeId = jsonObj.getString("node_id")
                    }
                    if (jsonObj.has("avatar_url")) {
                        avatar = jsonObj.getString("avatar_url")

                        // lấy data dạng bitmap từ url: nghĩa là, truyền vào
                        // 1 url -> trả về là 1 bitmap
                        if (i == 0) {
                            thread {
                                val client = OkHttpClient.Builder().build()
                                val builder = Request.Builder()
                                builder.url(avatar)

                                val byteArray = client.newCall(builder.build()).execute().body?.bytes()

                                runOnUiThread {
                                    byteArray?.let {
                                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                        imageView.setImageBitmap(bitmap)
                                    }
                                }
                                Log.d("3igZeus_Image", "byteArray = $byteArray")
                            }
                        }
                    }
                }
                Log.d("3igZeus", "$i user = $user")
                users.add(user)
            }
        }

        // bài tập: Hãy lấy data về. => covert thành model
        // => Hiển thị lên recyclerView cho mình.

    }
}