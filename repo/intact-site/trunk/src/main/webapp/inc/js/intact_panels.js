function intact_toggleVisibility(panelId)
{
    var panel = document.getElementById(panelId);
    var currentVisibility = panel.style.visibility;

    if (currentVisibility == 'hidden')
    {
        panel.style.visibility = 'visible';
    }
    else
    {
        panel.style.visibility = 'hidden';
    }
}

function intact_clearAndFocusField(fieldId)
{
    var field = document.getElementById(fieldId);

    field.value = '';
    field.focus();
}