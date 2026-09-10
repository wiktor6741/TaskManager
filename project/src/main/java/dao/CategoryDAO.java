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
        String sql = "SELECT * FROM categories WHERE deleted_at IS NULL";
        List<Category> categories = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                Category category = new Category(rs.getString("categoryName"));
                category.setId(rs.getInt("categoryID"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public void addCategory(Category category){
        String sql = """
                        INSERT INTO categories
                        (category_id, category_name, description)
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
                UPDATE categories
                SET 
                    category_name = ?,
                    description = ?
                WHERE category_id = ?
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
                UPDATE categories
                SET deleted_at = strftime('%Y-%m-%dT%H:%M:00', 'now')
                WHERE category_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void clear(){
        String sql = "DELETE FROM categories";
        try (Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

