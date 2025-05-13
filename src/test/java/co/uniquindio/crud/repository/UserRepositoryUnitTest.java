package co.uniquindio.crud.repository;

import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioRepositoryTest {

    UsuarioRepository usuarioRepository;

    Usuario usuarioActivo;
    Usuario usuarioEliminado;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);

        usuarioActivo = new Usuario();
        usuarioActivo.setId(1L);
        usuarioActivo.setNombre("Juan Pérez");
        usuarioActivo.setCedula("123456789");
        usuarioActivo.setEmail("juan@uniquindio.edu.co");
        usuarioActivo.setEstadoCuenta(EstadoCuenta.ACTIVADA);

        usuarioEliminado = new Usuario();
        usuarioEliminado.setId(2L);
        usuarioEliminado.setNombre("Pedro Gómez");
        usuarioEliminado.setCedula("987654321");
        usuarioEliminado.setEmail("pedro@uniquindio.edu.co");
        usuarioEliminado.setEstadoCuenta(EstadoCuenta.ELIMINADA);

        // Configurar comportamiento del mock
        when(usuarioRepository.findActiveById(1L)).thenReturn(usuarioActivo);
        when(usuarioRepository.findActiveById(2L)).thenReturn(null);

        when(usuarioRepository.findByEmail("juan@uniquindio.edu.co")).thenReturn(Optional.of(usuarioActivo));
        when(usuarioRepository.findByEmail("pedro@uniquindio.edu.co")).thenReturn(Optional.empty());

        when(usuarioRepository.findByCedula("123456789")).thenReturn(Optional.of(usuarioActivo));
        when(usuarioRepository.findByCedula("987654321")).thenReturn(Optional.empty());

        when(usuarioRepository.findActiveUsersPaged(1, 10)).thenReturn(List.of(usuarioActivo));
    }

    @Test
    void testFindActiveById() {
        Usuario resultado = usuarioRepository.findActiveById(1L);
        assertNotNull(resultado);
        assertEquals("juan@uniquindio.edu.co", resultado.getEmail());

        Usuario eliminado = usuarioRepository.findActiveById(2L);
        assertNull(eliminado);
    }

    @Test
    void testFindByEmail() {
        Optional<Usuario> resultado = usuarioRepository.findByEmail("juan@uniquindio.edu.co");
        assertTrue(resultado.isPresent());

        Optional<Usuario> eliminado = usuarioRepository.findByEmail("pedro@uniquindio.edu.co");
        assertFalse(eliminado.isPresent());
    }

    @Test
    void testFindByCedula() {
        Optional<Usuario> resultado = usuarioRepository.findByCedula("123456789");
        assertTrue(resultado.isPresent());

        Optional<Usuario> eliminado = usuarioRepository.findByCedula("987654321");
        assertFalse(eliminado.isPresent());
    }

    @Test
    void testFindActiveUsersPaged() {
        List<Usuario> resultados = usuarioRepository.findActiveUsersPaged(1, 10);
        assertEquals(1, resultados.size());
        assertEquals("juan@uniquindio.edu.co", resultados.get(0).getEmail());
    }
}

