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