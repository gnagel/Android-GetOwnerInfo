import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class OwnerInfo {
	// this class allows to get device information. It's done in two steps:
	// 1) get synchronization account email
	// 2) get contact data, associated with this email
	// by https://github.com/jehy

	public String id = null;
	public String email = null;
	public String phone = null;
	public String accountName = null;
	public String name = null;

	public OwnerInfo(Activity MainActivity) {
		final AccountManager manager = AccountManager.get(MainActivity);
		final Account[] accounts = manager.getAccountsByType("com.google");
		if (accounts[0].name != null) {
			accountName = accounts[0].name;

			ContentResolver cr = MainActivity.getContentResolver();
			Cursor emailCur = cr.query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Email.DATA + " = ?",
					new String[] { accountName }, null);
			while (emailCur.moveToNext()) {
				id = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
				email = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				String newName = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (name == null || newName.length() > name.length())
					name = newName;

				Log.v("Got contacts", "ID " + id + " Email : " + email
						+ " Name : " + name);
			}

			emailCur.close();
			if (id != null) {

				// get the phone number
				Cursor pCur = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);
				while (pCur.moveToNext()) {
					phone = pCur
							.getString(pCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					Log.v("Got contacts", "phone" + phone);
				}
				pCur.close();
			}
		}
	}
}