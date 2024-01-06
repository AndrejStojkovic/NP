// K2 37

import java.util.*;

class Comment implements Comparable<Comment> {
    String commentId;
    String username;
    String content;
    ArrayList<Comment> comments;
    int likes;

    Comment(String commentId, String username, String content) {
        this.commentId = commentId;
        this.username = username;
        this.content = content;
        comments = new ArrayList<>();
        likes = 0;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void likeComment() {
        likes++;
    }

    public String print(int level) {
        StringBuilder str = new StringBuilder();
        String tab = "    " + "    ".repeat(level);

        str.append(tab).append("Comment: ").append(content).append("\n");
        str.append(tab).append("Written by: ").append(username).append("\n");
        str.append(tab).append("Likes: ").append(likes).append("\n");
        comments.stream().sorted(Comparator.reverseOrder()).forEach(x -> str.append(x.print(level + 1)));

        return str.toString();
    }

    public int getTotalLikes() {
        int total = likes;
        for (Comment comment : comments) {
            total += comment.getTotalLikes();
        }
        return total;
    }

    public Comment findComment(String targetId) {
        if(commentId.equals(targetId)) {
            return this;
        }

        for(Comment comment : comments) {
            Comment found = comment.findComment(targetId);
            if(found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public int compareTo(Comment o) {
        return Integer.compare(getTotalLikes(), o.getTotalLikes());
    }
}

class Post {
    String username;
    String postContent;
    ArrayList<Comment> comments;

    Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        comments = new ArrayList<>();
    }

    public void addComment(String username, String commentId, String content, String replyToId) {
        Comment comment = new Comment(commentId, username, content);

        if(replyToId == null) {
            comments.add(comment);
            return;
        }

        Comment target = findComment(replyToId);
        if(target != null) {
            target.addComment(comment);
        }
    }

    public void likeComment(String commentId) {
        Comment comment = findComment(commentId);
        if(comment != null) {
            comment.likeComment();
        }
    }

    private Comment findComment(String commentId) {
        for(Comment comment : comments) {
            Comment found = comment.findComment(commentId);
            if(found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Post: ").append(postContent).append("\n");
        str.append("Written by: ").append(username).append("\n");
        str.append("Comments:\n");

        comments.stream().sorted(Comparator.reverseOrder()).forEach(x -> str.append(x.print(1)));

        return str.toString();
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
