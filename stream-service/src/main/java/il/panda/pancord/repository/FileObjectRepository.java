package il.panda.pancord.repository;

import il.panda.pancord.models.entity.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileObjectRepository extends JpaRepository<FileObject, UUID> {
}
