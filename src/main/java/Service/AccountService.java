package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDao;
import DAO.DaoException;
import Model.Account;

/**
 * AccountService handles the business logic for managing accounts, including validation
 * and CRUD operations, while interacting with the AccountDao for database actions.
 */
public class AccountService {

    private final AccountDao accountDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    /**
     * Default constructor initializing AccountDao.
     */
    public AccountService() {
        this.accountDao = new AccountDao();
    }

    /**
     * Constructor for injecting a custom AccountDao instance, often used for testing.
     *
     * @param accountDao Custom DAO implementation.
     */
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Fetches an account by its ID.
     *
     * @param id The unique identifier of the account.
     * @return An Optional containing the account if found.
     * @throws ServiceException If any issues occur during retrieval.
     */
    public Optional<Account> getAccountById(int id) {
        LOGGER.info("Retrieving account with ID: {}", id);
        try {
            Optional<Account> account = accountDao.getById(id);
            LOGGER.info("Account retrieved: {}", account.orElse(null));
            return account;
        } catch (DaoException e) {
            throw new ServiceException("Error while retrieving account", e);
        }
    }

    /**
     * Retrieves all accounts from the database.
     *
     * @return A list of accounts.
     * @throws ServiceException If an error occurs during retrieval.
     */
    public List<Account> getAllAccounts() {
        LOGGER.info("Retrieving all accounts");
        try {
            List<Account> accounts = accountDao.getAll();
            LOGGER.info("Retrieved {} accounts", accounts.size());
            return accounts;
        } catch (DaoException e) {
            throw new ServiceException("Error while fetching all accounts", e);
        }
    }

    /**
     * Searches for an account by username.
     *
     * @param username The username of the account to search.
     * @return An Optional containing the account if found.
     * @throws ServiceException If an error occurs during the search.
     */
    public Optional<Account> findAccountByUsername(String username) {
        LOGGER.info("Searching for account with username: {}", username);
        try {
            Optional<Account> account = accountDao.findAccountByUsername(username);
            LOGGER.info("Account found: {}", account.orElse(null));
            return account;
        } catch (DaoException e) {
            throw new ServiceException("Error while searching account with username: " + username, e);
        }
    }

    /**
     * Validates login credentials for an account.
     *
     * @param account The account with credentials to validate.
     * @return An Optional containing the account if validation succeeds.
     * @throws ServiceException If validation fails or an error occurs.
     */
    public Optional<Account> validateLogin(Account account) {
        LOGGER.info("Validating login for account");
        try {
            Optional<Account> validatedAccount = accountDao.validateLogin(account.getUsername(), account.getPassword());
            LOGGER.info("Login validation successful: {}", validatedAccount.isPresent());
            return validatedAccount;
        } catch (DaoException e) {
            throw new ServiceException("Error during login validation", e);
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to be created.
     * @return The newly created account.
     * @throws ServiceException If creation fails or validation errors occur.
     */
    public Account createAccount(Account account) {
        LOGGER.info("Creating new account: {}", account);
        try {
            validateAccount(account);
            if (findAccountByUsername(account.getUsername()).isPresent()) {
                throw new ServiceException("An account with this username already exists");
            }
            Account createdAccount = accountDao.insert(account);
            LOGGER.info("Account successfully created: {}", createdAccount);
            return createdAccount;
        } catch (DaoException e) {
            throw new ServiceException("Error while creating account", e);
        }
    }

    /**
     * Updates an existing account.
     *
     * @param account The account details to update.
     * @return True if the update was successful, false otherwise.
     * @throws ServiceException If updating fails.
     */
    public boolean updateAccount(Account account) {
        LOGGER.info("Updating account: {}", account);
        try {
            boolean isUpdated = accountDao.update(account);
            LOGGER.info("Account updated: {}. Success: {}", account, isUpdated);
            return isUpdated;
        } catch (DaoException e) {
            throw new ServiceException("Error while updating account", e);
        }
    }

    /**
     * Deletes a specific account.
     *
     * @param account The account to delete.
     * @return True if deletion was successful, false otherwise.
     * @throws ServiceException If deletion fails.
     */
    public boolean deleteAccount(Account account) {
        LOGGER.info("Deleting account: {}", account);
        if (account.getAccount_id() == 0) {
            throw new IllegalArgumentException("Account ID must be specified");
        }
        try {
            boolean isDeleted = accountDao.delete(account);
            LOGGER.info("Account deleted: {}. Success: {}", account, isDeleted);
            return isDeleted;
        } catch (DaoException e) {
            throw new ServiceException("Error while deleting account", e);
        }
    }

    /**
     * Validates the account data based on predefined business rules.
     *
     * @param account The account to validate.
     * @throws ServiceException If validation fails.
     */
    private void validateAccount(Account account) {
        LOGGER.info("Validating account details: {}", account);
        try {
            String username = account.getUsername().trim();
            String password = account.getPassword().trim();

            if (username.isEmpty()) {
                throw new ServiceException("Username cannot be empty");
            }

            if (password.isEmpty()) {
                throw new ServiceException("Password cannot be empty");
            }

            if (password.length() < 4) {
                throw new ServiceException("Password must have at least 4 characters");
            }

            if (accountDao.doesUsernameExist(username)) {
                throw new ServiceException("The username already exists");
            }
        } catch (DaoException e) {
            throw new ServiceException("Error during account validation", e);
        }
    }

    /**
     * Checks if an account exists based on its ID.
     *
     * @param accountId The ID of the account to check.
     * @return True if the account exists, false otherwise.
     * @throws ServiceException If an error occurs during the check.
     */
    public boolean accountExists(int accountId) {
        LOGGER.info("Checking existence of account with ID: {}", accountId);
        try {
            boolean exists = accountDao.getById(accountId).isPresent();
            LOGGER.info("Account existence: {}", exists);
            return exists;
        } catch (DaoException e) {
            throw new ServiceException("Error while checking account existence", e);
        }
    }
}
