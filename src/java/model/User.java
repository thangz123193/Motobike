package model;

import java.util.Date;

public class User {
    private int userID;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private int roleID;
    private String roleName;   // populated via JOIN, convenience field
    private boolean isActive;
    private Date createdDate;

    public User() {
    }

    public User(int userID, String username, String password, String fullName, String email,
                String phone, String address, int roleID, boolean isActive) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.roleID = roleID;
        this.isActive = isActive;
    }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getRoleID() { return roleID; }
    public void setRoleID(int roleID) { this.roleID = roleID; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    // Convenience role checks, used heavily in JSP via EL: ${user.admin}, ${user.staff}, ${user.customer}
    public boolean isAdmin() { return roleID == 1; }
    public boolean isStaff() { return roleID == 2; }
    public boolean isCustomer() { return roleID == 3; }
}
