

$('li a').click(function()
{
  $('li').removeClass('active');
  $(this).parent().addClass('active');
});
