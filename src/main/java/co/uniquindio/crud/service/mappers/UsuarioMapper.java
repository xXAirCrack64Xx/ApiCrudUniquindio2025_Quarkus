package co.uniquindio.crud.service.mappers;

import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.OcupacionUsuario;
import co.uniquindio.crud.entity.RolUsuario;
import co.uniquindio.crud.entity.Usuario;
import org.mapstruct.*;
import java.time.LocalDateTime;
import org.mindrot.jbcrypt.BCrypt;


@Mapper(componentModel = "cdi",
        imports = {LocalDateTime.class, BCrypt.class, OcupacionUsuario.class, RolUsuario.class, EstadoCuenta.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    UsuarioResponseDTO toResponseDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true) // El id se autogenera
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "cedula", source = "cedula")
    @Mapping(target = "email", source = "email")
    // Convierte la cadena del DTO en la enumeración correspondiente
    @Mapping(target = "ocupacion", expression = "java(OcupacionUsuario.valueOf(usuarioDTO.ocupacion()))")
    // Establece un rol por defecto
    @Mapping(target = "rol", expression = "java(RolUsuario.GUEST)")
    // Establece el estado de cuenta por defecto
    @Mapping(target = "estadoCuenta", expression = "java(EstadoCuenta.REGISTRADA)")
    @Mapping(target = "clase", source = "clase")
    // Codifica la contraseña usando BCrypt
    @Mapping(target = "clave", expression = "java(BCrypt.hashpw(usuarioDTO.clave(), BCrypt.gensalt()))")
    @Mapping(target = "fechaCreacion", expression = "java(LocalDateTime.now())")
    @Mapping(target = "fechaActualizacion", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);

    @Mapping(target = "fechaActualizacion", expression = "java(LocalDateTime.now())")
    void updateEntityFromDTO(UsuarioDTO usuarioDTO, @MappingTarget Usuario usuario);
}

