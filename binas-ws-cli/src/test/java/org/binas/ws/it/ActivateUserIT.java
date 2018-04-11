package org.binas.ws.it;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.Assert;
import org.junit.Test;

public class ActivateUserIT extends BaseIT {

	@Test
	public void success() throws EmailExists_Exception, InvalidEmail_Exception {
		UserView user = client.activateUser("a.c@b.d");

		Assert.assertNotNull(user);
		Assert.assertEquals("a.c@b.d", user.getEmail());
		Assert.assertEquals(0, (int) user.getCredit());
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void emptyEmail() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void nullEmail() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(null);
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail1() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("a.b");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail2() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("@");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail3() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("a@");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail4() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("@b");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail5() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("a.@b");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail6() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(".a@b");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail7() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("a@b.");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void invalidEmail8() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("a@.b");
	}

}
