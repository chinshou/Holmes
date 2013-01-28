var Application = (function (application) {
	application.Views.PictureFolderListView = Backbone.View.extend({
		el : $("#main_content"),
		initialize : function () {
			this.template = $("#folder_list_template").html();
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
			this.collection.bind('add', this.render);
			this.collection.bind('remove', this.render);			
		},
		render : function () {
			var renderedContent = Mustache.to_html(this.template,
				{
					folders : this.collection.toJSON(),
					title : $.i18n.prop("msg.picture.title"),
					nameLabel : $.i18n.prop("msg.name"),
					pathLabel : $.i18n.prop("msg.path"),
					addLabel : $.i18n.prop("msg.add"),
					editLabel : $.i18n.prop("msg.edit"),
					removeLabel : $.i18n.prop("msg.remove"),
					editTarget : "editPictureFolder",
					addTarget : "addPictureFolder",
					removeTarget : "removePictureFolder",
					removeConfirm : $.i18n.prop("msg.picture.remove.confirm")
				}
			);
			this.$el.html(renderedContent);
		},
		events : {
			"click #addPictureFolder" : "onAddPictureFolder"
		},
		onAddPictureFolder : function() {
			var that = this;
			var folderLabel = $("#main_content > [name='folderLabel']").val();
			var folderPath = $("#main_content > [name='folderPath']").val();
			var newPictureFolder = new Application.Models.PictureFolders();
			newPictureFolder.save({
						"id" : null,
						"name" : folderLabel,
						"path" : folderPath
					},{
						success : function() {
							that.collection.fetch({
								success:function() {
									that.render();
								}
							});
						},
						error : function(model, response) {
							console.log("save error");
							console.log(model);
							console.log(response);
						}
					});
		}
	});
	return application;
}(Application));