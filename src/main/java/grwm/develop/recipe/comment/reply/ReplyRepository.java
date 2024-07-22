package grwm.develop.recipe.comment.reply;

import grwm.develop.recipe.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByComment(Comment comment);
}
