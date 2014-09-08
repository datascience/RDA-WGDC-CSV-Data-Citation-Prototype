$(document).ready(
		function() {
			$('#selectDB_dropdown').click(
					function() {
						console.log("change");
						var selectedValue = $(this).val();
						var servletUrl = 'metadata?currentDatabase='
								+ selectedValue;

						$.getJSON(servletUrl, function(options) {
							var dropdown2 = $('#selectTable_dropdown');
							$('>option', dropdown2).remove(); // Clean old
																// options
																// first.
							if (options) {
								$.each(options, function(key, value) {
									dropdown2.append($('<option/>').val(key)
											.text(value));
								});
							} else {
								dropdown2.append($('<option/>').text(
										"Please select the database first"));
							}
						});
					});
		});