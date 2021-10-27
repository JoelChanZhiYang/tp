package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.ClaimCommand.EditClaimDescriptor;
import static seedu.address.logic.commands.ClaimCommand.MESSAGE_CLAIM_ADDED_SUCCESS;
import static seedu.address.logic.commands.ClaimCommand.MESSAGE_CLAIM_EDITED_SUCCESS;
import static seedu.address.logic.commands.ClaimCommand.MESSAGE_CLAIM_EDIT_FAILURE;
import static seedu.address.logic.commands.ClaimCommand.MESSAGE_CLAIM_REMOVED_SUCCESS;
import static seedu.address.logic.commands.CommandTestUtil.CLAIM_AMY;
import static seedu.address.logic.commands.CommandTestUtil.CLAIM_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLAIM_STATUS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLAIM_TITLE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.claim.Title;
import seedu.address.model.person.Person;
import seedu.address.testutil.ClaimBuilder;

public class ClaimCommandTest {

    private ClaimBuilder claimBuilderAmy = new ClaimBuilder(CLAIM_AMY);
    private ClaimBuilder claimBuilderBob = new ClaimBuilder(CLAIM_BOB);

    @Test
    public void execute_validInput_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToAddClaim = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        ClaimCommand claimCommand = new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor());

        Person personAfterAddClaim = new Person(personToAddClaim, Set.of(CLAIM_AMY));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToAddClaim, personAfterAddClaim);

        String expectedMessage = String.format(MESSAGE_CLAIM_ADDED_SUCCESS, CLAIM_AMY);

        assertCommandSuccess(claimCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeClaim_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        Person personToAddClaim = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Person personAfterAddClaim = new Person(personToAddClaim, Set.of(CLAIM_AMY));

        model.setPerson(personToAddClaim, personAfterAddClaim);

        ClaimCommand claimCommand = new ClaimCommand(INDEX_FIRST_PERSON,
                new EditClaimDescriptor(new Title(VALID_CLAIM_TITLE_AMY)));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        String expectedMessage = String.format(MESSAGE_CLAIM_REMOVED_SUCCESS, CLAIM_AMY);

        assertCommandSuccess(claimCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editClaim_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        Person personToAddClaim = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Person personAfterAddClaim = new Person(personToAddClaim, Set.of(CLAIM_AMY));

        model.setPerson(personToAddClaim, personAfterAddClaim);

        ClaimBuilder updatedClaimBuilder = new ClaimBuilder(CLAIM_AMY)
                .withDescription(VALID_CLAIM_STATUS_BOB);

        ClaimCommand claimCommand = new ClaimCommand(INDEX_FIRST_PERSON,
                updatedClaimBuilder.buildEditClaimDescriptor());

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        Person personAfterEditedClaim = new Person(personToAddClaim, Set.of(updatedClaimBuilder.buildClaim()));

        expectedModel.setPerson(personToAddClaim, personAfterEditedClaim);

        String expectedMessage = String.format(MESSAGE_CLAIM_EDITED_SUCCESS, updatedClaimBuilder.buildClaim());

        assertCommandSuccess(claimCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_incompleteClaim_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToAddClaim = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        ClaimCommand claimCommand = new ClaimCommand(INDEX_FIRST_PERSON,
                new EditClaimDescriptor(new Title(VALID_CLAIM_TITLE_AMY)));

        Person personAfterAddClaim = new Person(personToAddClaim, Set.of(CLAIM_AMY));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToAddClaim, personAfterAddClaim);

        String expectedMessage = String.format(MESSAGE_CLAIM_EDIT_FAILURE, VALID_CLAIM_TITLE_AMY);

        assertCommandFailure(claimCommand, model, expectedMessage);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ClaimCommand claimCommand = new ClaimCommand(outOfBoundIndex, claimBuilderAmy.buildEditClaimDescriptor());
        assertCommandFailure(claimCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        // same values -> return true
        assertEquals(new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor()),
                new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor()));

        // same object -> return true
        ClaimCommand claimCommand = new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor());
        assertEquals(claimCommand, claimCommand);

        // different index -> return false
        assertNotEquals(new ClaimCommand(INDEX_SECOND_PERSON, claimBuilderAmy.buildEditClaimDescriptor()),
                new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor()));

        // different claim -> return false
        assertNotEquals(new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderBob.buildEditClaimDescriptor()),
                new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor()));

        // different type -> return false
        assertNotEquals(new ClaimCommand(INDEX_FIRST_PERSON, claimBuilderAmy.buildEditClaimDescriptor()), 2);
    }
}
