<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create</title>

</head>
<body>
<span id="load_date"> </span>
<br>
<textarea style="  box-sizing: border-box;
  border: 2px solid #ccc;
  border-radius: 4px;
  background-color: #f8f8f8;
  font-size: 16px; width:500px; height:200px;" id="code_snippet"> //write your code here </textarea>
<br>
<button style="
  width: 80px;
  height: 50px;
  line-height: 42px;
  border-radius: 10px;
  border-color:red;
  border-style:bold;
  text-align: center;
" id="send_snippet" type="submit" onclick="send()">Submit</button>
<script>
    function send() {
    let object = {
        "code": document.getElementById("code_snippet").value
    };

    let json = JSON.stringify(object);

    let xhr = new XMLHttpRequest();
    xhr.open("POST", '/api/code/new', false)
    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    xhr.send(json);

    if (xhr.status == 200) {
      alert("Success!");
    }
}
    </script>
</body>
</html>