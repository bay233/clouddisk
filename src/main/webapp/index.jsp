<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的云盘</title>

<script type="text/javascript" src="js/jquery/jquery.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>

</head>
<body>
	<form >
		<input type="file" name="uploadFile" id="uploadFile">
		<input type="button" value="上传" onclick="uploadFileBtu()">
		<input type="hidden" id="currentPath" name="currentPath">
	</form>
	
	<input type="button" value="新建目录" onclick="mkdir()">
	<hr>
	<a href="javascript:void(0)" onclick="goUp()">返回上一层</a>
	当前路径为:
	<span id="pathDiv" data="hdfs://bay1:9000/">
		
	</span>
	共<span id="fileCount">xx</span>个
	<hr>
	<input type="button" value="图片" onclick="selectFile('1')">
	<input type="button" value="文档" onclick="selectFile('2')">
	<input type="button" value="音频" onclick="selectFile('3')">
	<input type="button" value="视频" onclick="selectFile('4')">
	<input type="button" value="文档" onclick="selectFile('0')">
	<hr>
	<table width="70%" border="1px">
		<thead>
			<tr>
				<td>文件名</td>
				<td>大小</td>
				<td>操作日期</td>
				<td>操作</td>
			</tr>
		</thead>
		<tbody id="fileList">
			
		</tbody>
	</table>

	<script type="text/javascript">
	
		function formatPath(path){
			var path_ = path.replace('hdfs://bay1:9000','');
			var path_split = "";
			var path_ = path_.split('/');
			path_split += "<a href='javascript:void(0)' onclick='show(\"/\")'>/</a>";
			var currentPath = "/";
			for ( i in path_){
				if (path_[i] != null && path_[i] != ""){
					currentPath += path_[i] + "/";
					path_split += " > <a href='javascript:void(0)' onclick='show(\""+ currentPath +"\")'>"+path_[i]+"</a>";
				}
			}
			return path_split;
		}
		
		function formatFileList(path, data){
			$('#fileCount').html(data.length);
			$('#pathDiv').html(formatPath(path));
			$('#pathDiv').attr("data",path);
			var result = "";
			for (i in data){
				result += "<tr>";
				result += "<td><a href='javascript:void(0)' onclick='show(\""+ data[i].filePath +"\")'>"+data[i].fileName+"</a></td>";
				if (data[i].isFile){
					result += "<td>"+data[i].sizeString+"</td>";
				}else{
					result += "<td></td>";
				}
				result += "<td>"+data[i].dateString+"</td>";
				result += "<td> <a href='javascript:void(0)' onclick='downloadFile(\""+ data[i].fileName +"\")'>下载</a> <a href='javascript:void(0)' onclick='copyTo(\""+ data[i].fileName +"\")'>复制</a>  <a href='javascript:void(0)' onclick='moveTo(\""+ data[i].fileName +"\")'>移动</a>  <a href='javascript:void(0)' onclick='rename(\""+ data[i].fileName +"\")'>重命名</a>  <a href='javascript:void(0)' onclick='delFile(\""+ data[i].fileName +"\")'>删除</a></td>";
				result += "</tr>";
			}
			$('#fileList').html(result);
		}
		
		function uploadFileBtu(){
			var currentPath = $('#pathDiv').attr('data');
			
			$.ajaxFileUpload({
				url:"upload.do",
				secureuri:false,
				fileElementId:"uploadFile",
				dataType:"json",
				data:{'currentPath':currentPath},
				success:function(data, status){
					formatFileList(currentPath, data.t);
				},
				error:function(data, status, e){
					alert("文件上传错误！");
				}
			});
		}
		
		
		
		function downloadFile(fileName){
			var currentPath = $('#pathDiv').attr('data');
			location.href="cloudFile.do?op=downloadFile&currentPath="+currentPath+"&fileName"+fileName;
		}
		
		function selectFile(type){
			var currentPath = $('#pathDiv').attr('data');
			$.post('cloudFile.do?op=selectFile',{
				'currentPath':currentPath,
				'type':type
			},function(data){
				if(data.code == 1){
					formatFileList(currentPath, data.t);
				}else if(data.code == 0){
					alert(data.t);
				}
				
			});
		}
		
		function copyTo(oldName){
			var currentPath = $('#pathDiv').attr('data');
			var newPath = prompt("请输入要复制到的目录名(全路径  /yc)");
			if (newPath == null || newPath == ""){
				alert("路径名不能为空！");
			}
			
			$.post('cloudFile.do?op=copyTo',{
				'oldName':oldName,
				'newPath':newPath,
				'currentPath':currentPath,
			},function(data){
				if(data.code == 1){
					formatFileList(newPath, data.t);
				}else if(data.code == 0){
					alert(data.t);
				}
				
			});
		}
		
		function moveTo(oldName){
			var currentPath = $('#pathDiv').attr('data');
			var newPath = prompt("请输入要移动到的目录名(全路径  /yc)");
			if (newPath == null || newPath == ""){
				alert("路径名不能为空！");
			}
			
			$.post('cloudFile.do?op=moveTo',{
				'oldName':oldName,
				'newPath':newPath,
				'currentPath':currentPath,
			},function(data){
				if(data.code == 1){
					formatFileList(newPath, data.t);
				}else if(data.code == 0){
					alert(data.t);
				}
				
			});
			
		}
		
		
		function mkdir(){
			var newName = prompt("请输入新的目录名");
			if (newName == null || newName == ""){
				alert("文件名不能为空！");
			}
			var currentPath = $('#pathDiv').attr("data");
			$.post('cloudFile.do?op=mkdir',{
				'newName':newName,
				'currentPath':currentPath,
			},function(data){
				if(data.code == 1){
					formatFileList(currentPath, data.t);
				}else if(data.code == 0){
					alert(data.t);
				}
				
			});
		}
		
		function rename(oldName){
			var newName = prompt("请输入新得文件名");
			if (newName == null || newName == ""){
				alert("文件名不能为空！");
			}
			var currentPath = $('#pathDiv').attr("data");
			$.post('cloudFile.do?op=rename',{
				'newName':newName,
				'currentPath':currentPath,
				'oldName':oldName
			},function(data){
				if(data.code == 1){
					formatFileList(currentPath, data.t);
				}else if(data.code == 0){
					alert(data.t);
				}
				
			});
		}
		
		function delFile(oldName){
			var currentPath = $('#pathDiv').attr('data');
			var b = confirm('是否确定删除' + oldName +'?');
			if (!b){
				return ;
			}
			$.post('cloudFile.do?op=delFile',{
				'currentPath':currentPath,
				'oldName':oldName
			},function(data){
				if (data.code == 0){
					alert(data.t);
				}else if (data.code == 1){
					formatFileList(currentPath, data.t);
				}
			});
		}
	
		function goUp(){
			var currentPath = $('#pathDiv').attr('data');
			$.post('cloudFile.do?op=goUp',{
				'path':currentPath
			},function(data){
				if (data.code == 0){
					alert(data.t);
				}else if (data.code == 1){
					var path = data.t['parentPath'];
					var dataFileList = data.t['sonFileList'];
					formatFileList(path, dataFileList);
				}
			});
		}
	
		function show(path) {
			$.post("cloudFile.do?op=showRoot", {
				'path':path
			}, function(data) {
				formatFileList(path, data);
			});
		}
		$(function() {
			show('/');
		});
	</script>
</body>
</html>