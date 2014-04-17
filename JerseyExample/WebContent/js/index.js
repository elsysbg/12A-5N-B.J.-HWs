function getAll(){
	$.ajax({
		url: "api/rest/teams",
		method: "GET",
		success: function(data){
			for(team in data){
				appendTeamDiv(data[team].name);
			}
		}
	});
}

function add(name){
	$.ajax({
		url: "api/rest/teams",
		method: "POST",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: "{ \"name\":\"" + name + "\", \"created\":" + new Date().getTime() + " }",
		success: function(){
			appendTeamDiv(name);
		},
		error: function(err){
			console.log(err);
		}
	});
}

function moveUp(name)
{
	$.ajax({
		url: "api/rest/teams/" + name + "/moveup",
		method: "POST",
		success: function(data){
			var el = $(".teamName:contains('" + name + "')").parent();
			el.insertBefore(el.prev());
		}
	});
}

function moveDown(name)
{
	$.ajax({
		url: "api/rest/teams/" + name + "/movedown",
		method: "POST",
		success: function(data){
			var el = $(".teamName:contains('" + name + "')").parent();
			el.insertAfter(el.next());
		}
	});
}

function del(name)
{
	$.ajax({
		url: "api/rest/teams/" + name,
		method: "DELETE",
		success: function(data){
			$(".teamName:contains('" + name + "')").parent().remove();
		}
	});
}

$(document).ready(function(){
	getAll();
});

function appendTeamDiv(name)
{
	var html = 
	'<div class="team">' +
		'<font class="teamName">' + name + '</font>' +
		'<div class="control">' +
			'<a href="javascript:moveUp(\'' + name + '\')"><img src="images/up.png"></img></a>' +
			'<a href="javascript:moveDown(\'' + name + '\')"><img src="images/down.png"></img></a>' +
			'<a href="javascript:del(\'' + name + '\')"><img src="images/delete.png"></img></a>' +
		'</div>' +
	'</div>';
	
	$("#teams").append(html);
}