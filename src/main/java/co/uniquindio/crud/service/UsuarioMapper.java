package co.uniquindio.crud.service;

import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.OcupacionUsuario;
import co.uniquindio.crud.entity.RolUsuario;
import co.uniquindio.crud.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;


@ApplicationScoped
public class UsuarioMapper {

    public UsuarioResponseDTO mapToDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getClase(),
                usuario.getFechaCreacion(),
                usuario.getFechaActualizacion(),
                usuario.getEstadoCuenta()
        );
    }

    // Mapea de UsuarioDTO a Usuario (requiere la contraseña ya encriptada)
    public Usuario mapToEntity(UsuarioDTO usuarioDTO, String claveEncriptada) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.nombre());
        usuario.setCedula(usuarioDTO.cedula());
        usuario.setEmail(usuarioDTO.email());
        usuario.setOcupacion(OcupacionUsuario.valueOf(usuarioDTO.ocupacion()));
        usuario.setRol(RolUsuario.GUEST);
        usuario.setEstadoCuenta(EstadoCuenta.REGISTRADA);
        usuario.setClase(usuarioDTO.clase());
        usuario.setClave(claveEncriptada);
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuario;
    }

    public void updateEntity(UsuarioDTO usuarioDTO, Usuario usuario) {
        if (usuarioDTO == null || usuario == null) {
            return;
        }
        usuario.setNombre(usuarioDTO.nombre());
        usuario.setCedula(usuarioDTO.cedula());
        usuario.setEmail(usuarioDTO.email());
        usuario.setOcupacion(OcupacionUsuario.valueOf(usuarioDTO.ocupacion()));
        usuario.setClase(usuarioDTO.clase());
        usuario.setFechaActualizacion(LocalDateTime.now());
    }



}
