package uk.ac.ebi.intact.editor.it;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/11</pre>
 */

public class CheckingIT extends EditorIT {

    @Autowired
    private LifecycleManager lifecycleManager;

    @Test
    public void changeCorrectionCommentUsingAnnotations() throws Exception {
        // Given: I am in experiment page and I want to update a correction comment using the annotation tab
        Publication publication = createRandomPublicationAs("curator");
        Experiment exp = publication.getExperiments().iterator().next();

        final String oldAnnotationText = "old comment";
        exp.addAnnotation(getMockBuilder().createAnnotation(oldAnnotationText, null, CvTopic.CORRECTION_COMMENT));
        
        getCorePersister().saveOrUpdate(publication);

        goToExperimentPage(exp.getAc());
        loginAs("curator", driver);

        // When: I update the annotation text and click on save
        clickOnTheExperimentAnnotationsTab();

        final String correctionCommentTextBoxId = "annotationsTable:0:0:annotationTxt";
        final WebElement correctionTextBox = driver.findElement(By.id(correctionCommentTextBoxId));

        correctionTextBox.click();
        correctionTextBox.clear();
        correctionTextBox.sendKeys(oldAnnotationText + ". New comment");

		driver.findElement(By.id("unsavedSaveButton")).click();
        waitUntilLoadingIsComplete();

        // Then: the correction comment should be updated
        final WebElement newCorrectionTextBox = driver.findElement(By.id(correctionCommentTextBoxId));
        assertThat(newCorrectionTextBox.getText(), is(equalTo(oldAnnotationText+". New comment")));
    }

    @Test
    public void changeToBeReviewedUsingAnnotations() throws Exception {
        // Given: I am in experiment page and I want to update a correction comment using the annotation tab
        Publication publication = createRandomPublicationAs("curator");
        Experiment exp = publication.getExperiments().iterator().next();
        
        lifecycleManager.getCurationInProgressStatus().readyForChecking(publication, "Ready!", true);
        lifecycleManager.getReadyForCheckingStatus().reject(publication, "This is wrong!");

        setToBeReviewed(exp);
        getCorePersister().saveOrUpdate(publication);

        getEntityManager().flush();

        goToExperimentPage(exp.getAc());
        loginAs("reviewer", driver);

        // When: I update the annotation text and click on save
        clickOnTheExperimentAnnotationsTab();

        final String toBeReviewedTextBoxId = "annotationsTable:0:0:annotationTxt";
        final WebElement toBeReviewedTextBox = driver.findElement(By.id(toBeReviewedTextBoxId));
        
        String oldToBeReviewed = toBeReviewedTextBox.getText();

        toBeReviewedTextBox.click();
        toBeReviewedTextBox.clear();
        toBeReviewedTextBox.sendKeys(oldToBeReviewed + ". Yes, it is very wrong!");

        driver.findElement(By.id("unsavedSaveButton")).click();
        waitUntilLoadingIsComplete();

        // Then: the correction comment should be updated
        final WebElement newToBeReviewedBox = driver.findElement(By.id(toBeReviewedTextBoxId));
        assertThat(newToBeReviewedBox.getText(), is(equalTo(oldToBeReviewed+". Yes, it is very wrong!")));
    }

    private void setToBeReviewed(Experiment exp) {
        Annotation toBeReviewAnnot = new IntactMockBuilder().createAnnotation("This is wrong!", getDaoFactory().getCvObjectDao(CvTopic.class).getByShortLabel(CvTopic.TO_BE_REVIEWED));
        exp.addAnnotation(toBeReviewAnnot);
    }

    private Publication createRandomPublicationAs(String userName) {
        Publication publicationRandom = getMockBuilder().createPublicationRandom();
        lifecycleManager.getNewStatus().assignToCurator(publicationRandom, getUserByLogin(userName));
        lifecycleManager.getAssignedStatus().startCuration(publicationRandom);

        Experiment randomExp = getMockBuilder().createExperimentRandom(1);
        randomExp.setPublication(publicationRandom);
        randomExp.setBioSource(getDaoFactory().getBioSourceDao().getAll().get(0));
        publicationRandom.addExperiment(randomExp);
        return publicationRandom;
    }

    private void clickOnTheExperimentAnnotationsTab() {
        driver.findElement(By.xpath("//div[@id='experimentTabs']/ul/li[3]/a/em")).click();
    }
}
