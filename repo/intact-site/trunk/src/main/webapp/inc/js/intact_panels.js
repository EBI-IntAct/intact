function intact_clearAndFocusField(fieldId)
{
    var field = document.getElementById(fieldId);

    field.value = '';
    field.focus();
}