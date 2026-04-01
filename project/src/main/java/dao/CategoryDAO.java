package dao;

import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final Connection conn;

    public CategoryDAO(Connection conn){
        this.conn = conn;
    }


    public List<Category> getAllCategories(){
        String sql = "SELECT * FROM Categories";
        List<Category> categories = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                Category category = new Category(rs.getString("CategoryName"));
                category.setId(rs.getInt("CategoryID"));
                category.setDescription(rs.getString("Description"));
                categories.add(category);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public void addCategory(Category category){
        String sql = """
                        INSERT INTO Categories
                        (CategoryID, CategoryName, Description)
                        VALUES (?, ?, ?)
                        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setNull(1, Types.INTEGER);
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                }
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public void updateCategory(Category category) {
        System.out.println(category.getName());
        String sql = """
                UPDATE Categories
                SET 
                    CategoryName = ?,
                    Description = ?
                WHERE CategoryID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCategory(int id){
        String sql = """
                DELETE FROM Categories
                WHERE CategoryID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        String sql = "DELETE FROM Categories";
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

