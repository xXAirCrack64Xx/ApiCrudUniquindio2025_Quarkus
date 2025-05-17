package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.user.PaginacionUserResponseDTO;
import co.uniquindio.crud.dto.user.ParcialUserUpdate;
import co.uniquindio.crud.dto.user.UsuarioDTO;
import co.uniquindio.crud.dto.user.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO getUsuarioById(Long id);
    PaginacionUserResponseDTO getAllUsuariosPaginados(int page, int size);
    UsuarioResponseDTO createUsuario(UsuarioDTO usuarioDTO);
    UsuarioResponseDTO updateUsuario(Long id, UsuarioDTO usuarioDTO);
    UsuarioResponseDTO partialUpdateUsuario(Long id, ParcialUserUpdate dto) ;
    void deleteUsuario(Long id);

}
