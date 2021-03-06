var Application = (function(application) {
	application.Views.AudioFolderListView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function() {
			this.template = $.getTemplate("folderList.html");
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
			this.collection.bind('add', this.render);
			this.collection.bind('remove', this.render);
		},
		render : function() {
			var renderedContent = Mustache.to_html(this.template, {
				folders : this.collection.toJSON(),
				title : $.i18n.prop("msg.admin.audio.list.title"),
				description : $.i18n.prop("msg.admin.audio.list.description"),
				nameLabel : $.i18n.prop("msg.admin.name"),
				pathLabel : $.i18n.prop("msg.admin.path"),
				addLabel : $.i18n.prop("msg.admin.add"),
				editLabel : $.i18n.prop("msg.admin.edit"),
				removeLabel : $.i18n.prop("msg.admin.remove"),
				saveLabel : $.i18n.prop("msg.admin.save"),
				cancelLabel : $.i18n.prop("msg.admin.cancel"),
				browsable : true,
				dialogId : "audioDlg",
				removeTarget : "audioFolderRemove",
				icon : "music"
			});
			this.$el.html(renderedContent);
			$(".audioDlgEditOpen").tooltip({delay:{ show: 1000, hide: 0}});
			$(".audioFolderRemove").tooltip({delay:{ show: 1000, hide: 0}});
			$(".audioDlgAddOpen").tooltip({delay:{ show: 1000, hide: 0}});
		},
		events : {
			"click .audioDlgAddOpen" : "onAudioDlgAddOpen",
			"click a.audioDlgEditOpen" : "onAudioDlgEditOpen",
			"dblclick tr.audioDlgEditOpen" : "onAudioDlgEditOpen",
			"click .audioDlgClose" : "onAudioDlgClose",
			"click .audioDlgSave" : "onAudioDlgSave",
			"click .audioDlgBrowse" : "onAudioDlgBrowse",
			"click .audioFolderRemove" : "onAudioFolderRemove",
		},
		// open add audio folder dialog
		onAudioDlgAddOpen : function() {
			// initialize dialog 
			$("#audioDlgHeader").html($.i18n.prop("msg.admin.audio.add.title"));
			$("#folderId").val("");
			$("#folderName").val("");
			$("#folderPath").val("");
			this.showDialog();
			return false;
		},
		// open edit audio folder dialog
		onAudioDlgEditOpen : function(event) {
			var that = this;
			var folderId = $(event.currentTarget).data('id');
			// get audio folder
			var audioFolder = new Application.Models.AudioFolder({id : folderId});
			audioFolder.fetch({
				success : function(model) {
					// initialize dialog 
					$("#audioDlgHeader").html($.i18n.prop("msg.admin.audio.update.title"));
					$("#folderId").val(model.get('id'));
					$("#folderName").val(model.get('name'));
					$("#folderPath").val(model.get('path'));
					that.showDialog();
				},
				error : function(model,response) {
					bootbox.alert(response.responseText || response.statusText);
				}
			});
			return false;
		},
		// close dialog
		onAudioDlgClose : function() {
			folderSelectBox.hide();
			this.hideDialog();
			return false;
		},
		// save audio folder
		onAudioDlgSave : function() {
			var that = this;
			var folderId = $("#folderId").val();
			var folderName = $("#folderName").val().trim();
			var folderPath = $("#folderPath").val().trim();
			var audioFolder;
			folderSelectBox.hide();
			if (folderId === "") {
				// this is a new audio folder
				audioFolder = new Application.Models.AudioFolder();
			} else {
				// modify existing audio folder
				audioFolder = new Application.Models.AudioFolder({id:folderId});
			}
			// save audio folder
			audioFolder.save({
						"name" : folderName,
						"path" : folderPath
					},{
						success : function() {
							// close dialog
							that.hideDialog();
							// fetch collection
							that.collection.fetch();
						},
						error : function(model, response) {
							$("#messagebox").message({text: response.responseText || response.statusText, type: "danger"});
						}
					});
			return false;
		},
		// Show browse dialog
		onAudioDlgBrowse : function (){
			folderSelectBox.show($("#folderPath"));
			return false;
		},
		// remove audio folder
		onAudioFolderRemove : function(event){
			var that = this;
			// confirm dialog
			bootbox.confirm($.i18n.prop("msg.admin.audio.remove.confirm"),function(result) {
				if (result == true) {
					var folderId = $(event.currentTarget).data('id');
					var audioFolder = new Application.Models.AudioFolder({id : folderId});
					audioFolder.destroy({
						success : function() {
							// fetch collection
							that.collection.fetch();
						},
						error : function(model, response) {
							bootbox.alert(response.responseText || response.statusText);
						}
					});
				}
			}); 
			return false;
		},
		showDialog : function(){
			$("#messagebox").html("");
			$('#audioDlg').modal('show');
		},
		hideDialog : function(){
			$(".modal-backdrop").remove();
			$('#audioDlg').modal('hide');
		}
	});
	return application;
}(Application));